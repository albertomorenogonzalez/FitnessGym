package com.example.fitnessgym.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import com.example.fitnessgym.fragments.DatePickerFragment
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityRegisterBinding
import java.io.ByteArrayOutputStream
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    @SuppressLint("Range")
    private val launchGallery = registerForActivityResult(StartActivityForResult()) {

        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                /*duda, no funciona de la manera de los apuntes*/
                val data = result.data?.data

                binding.profilePick.setImageURI(data)
            }

    }

    private val launchCamera = registerForActivityResult(StartActivityForResult()) {
        result ->
            val bitmap = result.data?.extras?.get("data") as Bitmap

            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            binding.profilePick.setImageBitmap(bitmap)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val an = TranslateAnimation(1600.0f, 0.0f, 0.0f, 0.0f);
        an.duration = 1000;
        binding.scrollDownMessage.startAnimation(an);

        val timer = object : CountDownTimer(4000L, 1000L) {
            /**
             * Callback fired on regular interval.
             * @param millisUntilFinished The amount of time until finished.
             */
            override fun onTick(millisUntilFinished: Long) {
                Log.i("Timer", "${ millisUntilFinished / 1000L }")
            }

            /**
             * Callback fired when the time is up.
             */
            override fun onFinish() {
                val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                binding.scrollDownMessage.animation = fadeOut
                binding.scrollDownMessage.visibility = View.GONE
            }

        }

        timer.start()



        with (binding) {
            btnBirthDate.setOnClickListener { showDatePickerDialog() }
            profilePick.setOnClickListener { chooseGalleryOrPhoto() }
            editPhotoIcon.setOnClickListener { chooseGalleryOrPhoto() }

            registerBtn.setOnClickListener {
                val email = txtEmail.text.toString().trim()
                val pw = txtPw.text.toString().trim()
                val pwRepeat = txtPwRepeat.text.toString().trim()


                if (email.isNotEmpty() || email.isNotBlank() && pw.isNotEmpty() || pw.isNotBlank()) {
                    if (pw == pwRepeat) {
                        Firebase.auth
                            .createUserWithEmailAndPassword(email, pw)
                            .addOnSuccessListener {
                                finish()
                                return@addOnSuccessListener
                            }
                            .addOnFailureListener {
                                Snackbar.make(
                                    root,
                                    R.string.password_exception_1,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        Snackbar.make(root, R.string.password_exception_2, Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Snackbar.make(root, R.string.fields_exception, Snackbar.LENGTH_LONG).show()

                }

            }


            cancelBtn.setOnClickListener {
                finish()
                return@setOnClickListener
            }

        }


    }


    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }

        datePicker.show(supportFragmentManager, "datePicker")
    }

    @SuppressLint("SetTextI18n")
    fun onDateSelected(day: Int, month:Int, year:Int) {
        binding.birthdate.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
    }

    private fun chooseGalleryOrPhoto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.select_image)

        val options = arrayOf("Gallery", "Camera")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    requestPermissionForGallery()
                }
                1 -> {
                    requestPermissionForPhoto()
                }
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun requestPermissionForGallery() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickPhotoFromGallery()
            }
            else -> requestPermissionGalleryLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestPermissionForPhoto() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickPhoto()
            }
            else -> requestPermissionPhotoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionGalleryLauncher = registerForActivityResult(
        RequestPermission()
    ) {
            isGranted ->

        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionPhotoLauncher = registerForActivityResult(
        RequestPermission()
    ) {
            isGranted ->

        if (isGranted) {
            pickPhoto()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launchGallery.launch(intent)
    }

    private fun pickPhoto() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launchCamera.launch(intent)
    }


}