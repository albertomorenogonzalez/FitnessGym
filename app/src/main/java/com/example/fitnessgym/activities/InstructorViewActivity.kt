package com.example.fitnessgym.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityInstructorViewBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class InstructorViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstructorViewBinding

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInstructorViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        with (binding) {

            val instructor = intent.extras?.get("instructor") as Instructor
            val uid = instructor.uid

            val userDocRef = db.collection("usuarios").document(uid)
            var userCompleteName: String by Delegates.observable("") { _, _, newUserCompleteName ->
                profileName.text = newUserCompleteName
            }
            var userBirthdate: String by observable("") { _, _, newUserBirthdate ->
                userBirthdate.text = newUserBirthdate
            }
            var userEmail: String by observable("") { _, _, newUserEmail ->
                userEmail.text = newUserEmail
            }
            var userPhone: String by observable("") { _, _, newUserPhone ->
                userPhone.text = newUserPhone
            }
            var photoUrl: String by observable("") { _, _, newPhotoUrl ->
                val firebasePhotoStart =
                    "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                if (firebasePhotoStart in newPhotoUrl) {
                    Glide.with(binding.root).load(newPhotoUrl).into(profileProfilePick)
                } else {
                    profileProfilePick.setImageResource(R.drawable.fitness_gym_logo)
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
                        val newUserEmail = snapshot.getString("email") ?: ""
                        val newUserPhone = snapshot.getString("phone") ?: ""
                        val newPhotoUrl = snapshot.getString("photo") ?: ""

                        userCompleteName = "$newUserFirstName $newUserLastName"
                        supportActionBar?.title = userCompleteName
                        userBirthdate = Dates.showProperDate(newUserBirthdate)
                        userEmail = newUserEmail
                        userPhone = newUserPhone
                        photoUrl = newPhotoUrl
                    }
                }

            backButton.setOnClickListener {
                finish()
                return@setOnClickListener
            }

        }
    }
}
