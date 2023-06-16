package com.example.fitnessgym.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.entities.Group
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityGroupViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlin.properties.Delegates.observable

class GroupViewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGroupViewBinding

    private val db = FirebaseFirestore.getInstance()

    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        with (binding) {
            var group = intent.extras?.get("group") as Group

            val groupDocRef = db.collection("grupos").document(group.docId)

            var groupName: String by observable("") { _, _, newGroupName ->
                groupAddName.text = newGroupName
            }

            var instructorName: String by observable("") { _, _, newInstructorName ->
                groupAddInstructorName.text = newInstructorName
            }

            var groupDescription: String by observable("") { _, _, newGroupDescription ->
                groupAddDescription.text = newGroupDescription
            }


            var photoUrl: String by observable("") { _, _, newPhotoUrl ->
                if (!isDestroyed) {
                    val firebasePhotoStart =
                        "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                    if (firebasePhotoStart in newPhotoUrl) {
                        Glide.with(binding.root).load(newPhotoUrl).into(groupPick)
                    } else {
                        groupPick.setImageResource(R.drawable.fitness_gym_logo)
                    }
                }

            }

            groupDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val newGroupName = snapshot.getString("name") ?: ""
                    val newGroupDescription = snapshot.getString("description") ?: ""
                    val newGroupPhotoUrl = snapshot.getString("photo") ?: ""

                    groupName = newGroupName
                    supportActionBar?.title = groupName
                    groupDescription = newGroupDescription
                    photoUrl = newGroupPhotoUrl

                    val userDocRef = db.collection("usuarios").document(group.docMonitor)
                    userDocRef.get()
                        .addOnSuccessListener { groupSnapshot ->
                            if (groupSnapshot != null && groupSnapshot.exists()) {
                                val newInstructorName = groupSnapshot.getString("first_name") ?: ""
                                val newInstructorSurname = groupSnapshot.getString("last_name") ?: ""
                                instructorName = "$newInstructorName $newInstructorSurname"
                            }
                        }

                    group = Group(
                        0,
                        group.docId,
                        newGroupName,
                        group.docMonitor,
                        newGroupDescription,
                        newGroupPhotoUrl
                    )
                }
            }


            addEditGroupButton.setOnClickListener {
                val i = Intent(this@GroupViewActivity, AddEditGroupActivity::class.java)
                val form = "edit"
                i.putExtra("form", form)
                i.putExtra("group", group)
                answer.launch(i)
            }

            deleteGroup.setOnClickListener {
                MaterialAlertDialogBuilder(this@GroupViewActivity)
                    .setTitle(R.string.delete_user)
                    .setMessage(R.string.delete_group_message)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        db.collection("grupos").document(group.docId).delete()

                        setResult(RESULT_OK)
                        finish()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }




            cancelButton.setOnClickListener {
                finish()
                return@setOnClickListener
            }
        }
    }

    /**
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            RESULT_OK -> {
                Snackbar.make(binding.root, R.string.operation_completed, Snackbar.LENGTH_LONG).show()
            }
            RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }
}