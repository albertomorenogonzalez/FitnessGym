package com.example.fitnessgym.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
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
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.example.fitnessgym.fragments.DatePickerFragment
import com.example.fitnessgym.services.InstructorService
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.FileUtil
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val formatter : SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ss", Locale.GERMANY
    )
    private val now: Date = Date()
    private val fileName = formatter.format(now)
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference(
        "fitnessgym-images/$fileName"
    )
    private var imageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("Range")
    private val launchGallery = registerForActivityResult(StartActivityForResult()) {

        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.data

                if (data != null) {
                    imageUri = data
                }

                Glide.with(binding.root.context).load(data).into(binding.profilePick)
            }

    }

    private val launchCamera = registerForActivityResult(StartActivityForResult()) {
        result ->

            val bitmap = result.data?.extras?.get("data") as Bitmap

            saveImage(bitmap)

            Glide.with(binding.root.context).load(bitmap).into(binding.profilePick)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val an = TranslateAnimation(1600.0f, 0.0f, 0.0f, 0.0f);
        an.duration = 1000;
        binding.scrollDownMessage.startAnimation(an)

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
                val name = txtName.text.toString().trim()
                val surname = txtSurname.text.toString().trim()
                val birthdate = birthdate.text.toString().trim()
                val telephoneNumber = txtPhoneNumber.text.toString().trim()
                val dni = txtDNI.text.toString().trim()
                var photo : Uri? = null
                if (imageUri != null) {
                    photo = Uri.parse(Uri.decode(imageUri.toString()))
                }
                val email = txtEmail.text.toString().trim()
                val pw = txtPw.text.toString().trim()
                val pwRepeat = txtPwRepeat.text.toString().trim()

                if (email.isNotEmpty() || email.isNotBlank() && pw.isNotEmpty() || pw.isNotBlank()) {
                    if (pw == pwRepeat) {
                        Firebase.auth.createUserWithEmailAndPassword(email, pw)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user?.uid
                                val user = authResult.user
                                val tokenTask = user?.getIdToken(true)

                                tokenTask?.addOnSuccessListener { result ->
                                    val token = result.token

                                    if (photo != null) {
                                        storageReference.putFile(photo).addOnSuccessListener { uploadTask ->
                                            uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                                                val photoUrl = downloadUri.toString()
                                                if (uid != null && token != null) {
                                                    val ins = Instructor(uid, name, surname, birthdate, email, telephoneNumber, dni, photoUrl)
                                                    InstructorService.registerInstructor(db, ins, token)
                                                }
                                            }.addOnFailureListener { exception ->
                                                Log.e("Firebase Storage", "Error al obtener la URL de descarga", exception)
                                            }
                                        }.addOnFailureListener { exception ->
                                            Log.e("Firebase Storage", "Error al subir la foto", exception)
                                        }
                                    } else {
                                        if (uid != null && token != null) {
                                            val ins = Instructor(uid, name, surname, birthdate, email, telephoneNumber, dni)
                                            InstructorService.registerInstructor(db, ins, token)
                                        }
                                    }
                                }?.addOnFailureListener { exception ->
                                    Log.e("Firebase Auth", "Error al obtener el token de autenticación", exception)
                                }
                            }
                            .addOnFailureListener {
                                Snackbar.make(root, R.string.password_exception_1, Snackbar.LENGTH_LONG).show()
                            }

                        Firebase.auth.signOut()
                        val i = Intent()
                        val extras = Bundle().apply {
                            putString("email", email)
                            putString("pw", pw)
                        }
                        i.putExtras(extras)
                        setResult(Activity.RESULT_OK, i)
                        finish()
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


    private fun saveImage(bitmap: Bitmap) {
        val outputStream: OutputStream
        var file: File? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val values = ContentValues()

            val fileName = System.currentTimeMillis()

            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FitnessGym")
            values.put(MediaStore.Images.Media.IS_PENDING, 1)

            val collection: Uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.insert(collection, values)

            outputStream = uri?.let { resolver.openOutputStream(it) }!!

            imageUri = uri

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } else {
            val imageDir: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val fileName = "${System.currentTimeMillis()}.jpg"

            file = File(imageDir, fileName)

            outputStream = FileOutputStream(file)
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        /*Mensaje después de esto (?)*/

        outputStream.flush()
        outputStream.close()

        if (file != null) {
            MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)
        }

    }


}
