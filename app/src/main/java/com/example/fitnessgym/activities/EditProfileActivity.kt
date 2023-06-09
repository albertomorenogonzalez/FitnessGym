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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.example.fitnessgym.functions.Dates
import com.example.fitnessgym.services.InstructorService
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityEditProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private val formatter : SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ss", Locale.GERMANY
    )
    private val now: Date = Date()
    private val fileName = formatter.format(now)
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference(
        "fitnessgym-images/$fileName"
    )

    private lateinit var token: String
    private lateinit var photo: String

    @SuppressLint("Range")
    private val launchGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data?.data

            if (data != null) {
                imageUri = data
            }

            Glide.with(binding.root.context).load(data).into(binding.profilePick)
        }

    }

    private val launchCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->

        val bitmap = result.data?.extras?.get("data") as Bitmap

        saveImage(bitmap)

        Glide.with(binding.root.context).load(bitmap).into(binding.profilePick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.edit_profile)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener {
                photo = it.get("photo").toString()
            }
        }

        with (binding) {
            if (uid != null) {
                val userDocRef = db.collection("usuarios").document(uid)

                var userFirstName: String by observable("") { _, _, newUserFirstName ->
                    textUserFirstName.setText(newUserFirstName)
                }
                var userLastName: String by observable("") { _, _, newUserLastName ->
                    textUserLastName.setText(newUserLastName)
                }
                var userBirthdate: String by observable("") { _, _, newUserBirthdate ->
                    textUserBirthdate.setText(newUserBirthdate)
                }
                var userPhone: String by observable("") { _, _, newUserPhone ->
                    textUserPhone.setText(newUserPhone)
                }
                var userDni: String by observable("") { _, _, newUserDni ->
                    textUserDni.setText(newUserDni)
                }
                var userToken: String by observable("") { _,_, userToken ->
                    token = userToken
                }
                var photoUrl: String by observable("") { _, oldPhotoUrl, newPhotoUrl ->
                    if (!isDestroyed) {
                        val firebasePhotoStart =
                            "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                        if (firebasePhotoStart in newPhotoUrl) {
                            Glide.with(binding.root).load(newPhotoUrl).into(profilePick)
                        } else {
                            profilePick.setImageResource(R.drawable.fitness_gym_logo)
                        }
                    }
                }


                userDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val newUserFirstName = snapshot.getString("first_name") ?: ""
                        val newUserLastName = snapshot.getString("last_name") ?: ""
                        val newUserBirthdate = snapshot.getString("birthdate") ?: ""
                        val newUserPhone = snapshot.getString("phone") ?: ""
                        val newUserDni = snapshot.getString("dni") ?: ""
                        val newUserToken = snapshot.getString("token") ?: ""
                        val newPhotoUrl = snapshot.getString("photo") ?: ""

                        userFirstName = newUserFirstName
                        userLastName = newUserLastName
                        userBirthdate = newUserBirthdate
                        userPhone = newUserPhone
                        userDni = newUserDni
                        userToken = newUserToken
                        photoUrl = newPhotoUrl
                    }
                }

            }

            btnBirthDate.setOnClickListener { Dates.showDatePickerDialog(supportFragmentManager, textUserBirthdate) }

            profilePick.setOnClickListener { chooseGalleryOrPhoto() }
            editPhotoIcon.setOnClickListener { chooseGalleryOrPhoto() }

            editUserButton.setOnClickListener {

                if (imageUri != null) {
                    imageUri?.let { uri ->
                        storageReference.putFile(uri).addOnSuccessListener { uploadTask ->
                            uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                                val photoUrl = downloadUri.toString()
                                if (uid != null) {
                                    val ins = Instructor(uid,
                                        textUserFirstName.text.toString(),
                                        textUserLastName.text.toString(),
                                        textUserBirthdate.text.toString(),
                                        auth.currentUser?.email.toString(),
                                        textUserPhone.text.toString(),
                                        textUserDni.text.toString(),
                                        photoUrl)
                                    InstructorService.registerOrEditInstructor(db, ins, token)
                                }
                            }.addOnFailureListener { exception ->
                                Log.e("Firebase Storage", "Error al obtener la URL de descarga", exception)
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("Firebase Storage", "Error al subir la foto", exception)
                        }
                    }
                } else {
                    if (uid != null) {
                        val ins = Instructor(uid,
                            textUserFirstName.text.toString(),
                            textUserLastName.text.toString(),
                            textUserBirthdate.text.toString(),
                            auth.currentUser?.email.toString(),
                            textUserPhone.text.toString(),
                            textUserDni.text.toString(),
                            photo)
                        InstructorService.registerOrEditInstructor(db, ins, token)
                    }

                }

                val i = Intent()
                finish()
                setResult(Activity.RESULT_OK, i)
            }

            editUserCancelButton.setOnClickListener() {
                finish()
                return@setOnClickListener
            }

        }
    }


    private fun chooseGalleryOrPhoto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.select_image)

        val options = arrayOf("Gallery", "Camera", "Quit Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    requestPermissionForGallery()
                }
                1 -> {
                    requestPermissionForPhoto()
                }
                2 -> {
                    binding.profilePick.setImageResource(R.drawable.fitness_gym_logo)
                    photo = ""
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }
                else -> requestPermissionGalleryLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickPhotoFromGallery()
                }
                else -> requestPermissionGalleryLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
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
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
            isGranted ->

        if (isGranted) {
            pickPhoto()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg", "image/jpeg"))
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

        outputStream.flush()
        outputStream.close()

        if (file != null) {
            MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)
        }

    }
}
