package com.example.fitnessgym.activities

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityAddEditGroupBinding
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
import kotlin.properties.Delegates.observable

class AddEditGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditGroupBinding
    private val db = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null
    private lateinit var photo: String
    private val formatter : SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd-HH-mm-ss", Locale.GERMANY
    )
    private val now: Date = Date()
    private val fileName = formatter.format(now)
    private val storageReference: StorageReference = FirebaseStorage.getInstance().getReference(
        "fitnessgym-images/$fileName"
    )
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("Range")
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

    private val launchCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val bitmap = result.data?.extras?.get("data") as Bitmap
            saveImage(bitmap)
            Glide.with(binding.root.context).load(bitmap).into(binding.profilePick)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.add_customer)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        val form = intent.extras?.get("form")

        val userNameList: MutableList<String> by observable(mutableListOf()) { _, _, _ ->
            adapter.notifyDataSetChanged()
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, userNameList)

        with(binding) {
            profilePick.setOnClickListener { chooseGalleryOrPhoto() }
            addPhotoIcon.setOnClickListener { chooseGalleryOrPhoto() }
            cancelButton.setOnClickListener {
                finish()
                return@setOnClickListener
            }

            val userUIDList: MutableList<String> by observable(mutableListOf()) { _, _, _ ->
                adapter.notifyDataSetChanged()
            }

            val usersCollectionRef = db.collection("usuarios")

            var group = Group()

            if (form == "edit") {
                group = intent.extras?.get("group") as Group
            }

            textGroupInstructorName.adapter = adapter

            usersCollectionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                userNameList.clear()

                if (form == "add") {
                    userNameList.add(getString(R.string.select_instructor))

                    photo = ""
                } else {
                    textGroupName.setText(group.name)

                    usersCollectionRef.document(group.docMonitor).get().addOnSuccessListener {
                        val instructorName = "${it.get("first_name")} ${it.get("last_name")}"
                        textGroupInstructorName.setSelection(userNameList.indexOf(instructorName))
                    }

                    textGroupDescription.setText(group.description)
                    if (group.photo != "") {
                        Glide.with(root).load(group.photo).into(profilePick)
                    } else {
                        profilePick.setImageResource(R.drawable.fitness_gym_logo)
                    }
                    photo = group.photo.toString()

                    addEditGroupButton.setText(R.string.edit_group)
                }


                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        val uid = document.get("uid").toString()
                        val firstName = document.get("first_name").toString()
                        val lastName = document.get("last_name").toString()

                        val user = "$firstName $lastName"

                        userNameList.add(user)
                        userUIDList.add(uid)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            addEditGroupButton.setOnClickListener {
                val selectedItem = textGroupInstructorName.selectedItem

                var selectedItemIndex = 0

                if (form == "add") {
                    selectedItemIndex = userNameList.indexOf(selectedItem) - 1
                } else {
                    selectedItemIndex = userNameList.indexOf(selectedItem)
                }

                val selectedUid = userUIDList[selectedItemIndex]


                val uid = if (form == "add") {
                    db.collection("grupos").document().id
                } else {
                    group.docId
                }


                val name = textGroupName.text.toString()
                val description = textGroupDescription.text.toString()

                if (imageUri != null) {
                    storageReference.putFile(imageUri!!).addOnSuccessListener { uploadTask ->
                        uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                            val photoUrl = downloadUri.toString()

                            val newGroup = Group(
                                0,
                                uid,
                                name,
                                selectedUid,
                                description,
                                photoUrl
                            )

                            db.collection("grupos").document(uid).set(newGroup)
                        }
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, R.string.error_obtaining_photo_url, Snackbar.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Snackbar.make(binding.root, R.string.error_uploading_photo, Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    val newGroup = Group(
                        0,
                        uid,
                        name,
                        selectedUid,
                        description,
                        photo
                    )

                    db.collection("grupos").document(uid).set(newGroup)
                }

                setResult(RESULT_OK)
                finish()
            }
        }

    }


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
        val cameraPermission = Manifest.permission.CAMERA

        when {
            ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED -> {
                pickPhoto()
            }
            else -> {
                requestPermissionPhotoLauncher.launch(cameraPermission)
            }
        }
    }

    private val requestPermissionGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickPhotoFromGallery()
            } else {
                Toast.makeText(this, R.string.enable_permission, Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

            val collection: Uri =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.insert(collection, values)

            outputStream = uri?.let { resolver.openOutputStream(it) }!!

            imageUri = uri

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } else {
            val imageDir: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
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
