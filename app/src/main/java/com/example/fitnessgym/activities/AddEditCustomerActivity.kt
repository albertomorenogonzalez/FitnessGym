package com.example.fitnessgym.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityAddEditCustomerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddEditCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditCustomerBinding
    private val db = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private lateinit var photo: String
    private val formatter: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ss", Locale.GERMANY
    )
    private val now: Date = Date()
    private val fileName = formatter.format(now)
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference(
        "fitnessgym-images/$fileName"
    )

    // Register for activity result to handle gallery selection
    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.data

                if (data != null) {
                    imageUri = data
                }

                Glide.with(binding.root.context).load(data).into(binding.profilePick)
            }
        }

    // Register for activity result to handle camera capture
    private val launchCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val bitmap = result.data?.extras?.get("data") as Bitmap

            saveImage(bitmap)

            Glide.with(binding.root.context).load(bitmap).into(binding.profilePick)
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        supportActionBar?.setTitle(R.string.add_customer)

        val form = intent.extras?.get("form")

        with(binding) {
            btnBirthDate.setOnClickListener { Dates.showDatePickerDialog(supportFragmentManager, textUserBirthdate) }

            profilePick.setOnClickListener { chooseGalleryOrPhoto() }
            addPhotoIcon.setOnClickListener { chooseGalleryOrPhoto() }
        }

        if (form == "add") {
            // Handle addition of a new customer
            with(binding) {
                addEditCustomerButton.setOnClickListener {
                    val uid = db.collection("clientes").document().id
                    val name = textUserFirstName.text.toString()
                    val surname = textUserLastName.text.toString()
                    val email = textUserEmail.text.toString()
                    val birthdate = Dates.formatDate(binding.textUserBirthdate.text.toString())
                    val phoneNumber = textUserPhone.text.toString()
                    val postalCode = textUserPostalCode.text.toString()
                    val inscriptionDate = Dates.targetFormatter.format(now)

                    if (imageUri != null) {
                        // Upload the selected image to Firebase Storage
                        storageReference.putFile(imageUri!!).addOnSuccessListener { uploadTask ->
                            uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                                val photoUrl = downloadUri.toString()

                                val newCustomer = Customer(
                                    0,
                                    uid,
                                    name,
                                    surname,
                                    birthdate,
                                    email,
                                    phoneNumber,
                                    postalCode,
                                    photoUrl,
                                    inscriptionDate
                                )

                                db.collection("clientes").document(uid).set(newCustomer)
                            }
                        }.addOnFailureListener {
                            Snackbar.make(
                                binding.root,
                                R.string.error_obtaining_photo_url,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, R.string.error_uploading_photo, Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        val newCustomer = Customer(
                            0,
                            uid,
                            name,
                            surname,
                            birthdate,
                            email,
                            phoneNumber,
                            postalCode,
                            null,
                            inscriptionDate
                        )

                        db.collection("clientes").document(uid).set(newCustomer)
                    }

                    val i = Intent()
                    i.putExtra("form", form.toString())
                    i.putExtra("name", "customer")
                    setResult(Activity.RESULT_OK, i)
                    finish()
                }
            }
        } else {
            // Handle editing an existing customer
            val customer = intent.extras?.get("customer") as Customer

            photo = customer.photo.toString()

            with(binding) {
                if (customer.photo != "") {
                    Glide.with(root).load(customer.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                textUserFirstName.setText(customer.name)
                textUserLastName.setText(customer.surname)
                textUserEmail.setText(customer.email)
                textUserEmail.isClickable = false
                textUserEmail.isFocusable = false
                textUserBirthdate.setText(Dates.showProperDate(customer.birthdate))
                textUserPhone.setText(customer.phone)
                textUserPostalCode.setText(customer.postal_code)
                addEditCustomerButton.setText(R.string.edit_customer)

                addEditCustomerButton.setOnClickListener {
                    val uid = customer.docId
                    val name = textUserFirstName.text.toString()
                    val surname = textUserLastName.text.toString()
                    val email = textUserEmail.text.toString()
                    val birthdate = Dates.formatDate(binding.textUserBirthdate.text.toString())
                    val phoneNumber = textUserPhone.text.toString()
                    val postalCode = textUserPostalCode.text.toString()
                    val inscriptionDate = customer.inscription

                    if (imageUri != null) {
                        // Upload the selected image to Firebase Storage
                        storageReference.putFile(imageUri!!).addOnSuccessListener { uploadTask ->
                            uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                                val photoUrl = downloadUri.toString()

                                val editCustomer = Customer(
                                    0,
                                    uid,
                                    name,
                                    surname,
                                    birthdate,
                                    email,
                                    phoneNumber,
                                    postalCode,
                                    photoUrl,
                                    inscriptionDate
                                )

                                db.collection("clientes").document(uid).set(editCustomer)
                            }
                        }.addOnFailureListener {
                            Snackbar.make(
                                binding.root,
                                R.string.error_obtaining_photo_url,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Snackbar.make(binding.root, R.string.error_uploading_photo, Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        val editCustomer = Customer(
                            0,
                            uid,
                            name,
                            surname,
                            birthdate,
                            email,
                            phoneNumber,
                            postalCode,
                            photo,
                            inscriptionDate
                        )

                        db.collection("clientes").document(uid).set(editCustomer)
                    }

                    val i = Intent()
                    i.putExtra("form", form.toString())
                    i.putExtra("name", "customer")
                    setResult(Activity.RESULT_OK, i)
                    finish()
                }
            }
        }

        binding.addEditCustomerCancelButton.setOnClickListener {
            finish()
            return@setOnClickListener
        }
    }

    // Function to handle selecting an image from the gallery or taking a photo
    private fun chooseGalleryOrPhoto() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.select_image)

        val options = arrayOf(getString(R.string.gallery), getString(R.string.camera), getString(R.string.quit_photo))
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

    // Request permission to access the gallery
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

    // Request permission to access the camera
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

    // Permission launcher for gallery access
    private val requestPermissionGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhotoFromGallery()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher for camera access
    private val requestPermissionPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickPhoto()
        } else {
            Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
        }
    }

    // Launches the gallery to select an image
    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg", "image/jpeg"))
        launchGallery.launch(intent)
    }

    // Launches the camera to take a photo
    private fun pickPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launchCamera.launch(intent)
    }

    // Save the captured image to the device storage
    private fun saveImage(bitmap: Bitmap) {
        val outputStream: OutputStream
        var file: File? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$fileName")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + "FitnessGym"
            )
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = resolver.openOutputStream(imageUri!!)!!
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "FitnessGym"

            val fileDir = File(imagesDir)
            if (!fileDir.exists()) {
                fileDir.mkdir()
            }

            file = File(imagesDir, "IMG_$fileName.jpg")
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
