package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.fitnessgym.activities.ConfigActivity
import com.example.fitnessgym.activities.EditProfileActivity
import com.example.fitnessgym.activities.LoginActivity
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.properties.Delegates.observable

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Activity result launcher for EditProfileActivity
    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)

        // Change color of the settings icon in the menu
        val icon = menu.findItem(R.id.settings_button)
        icon.icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)

        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_button -> {
                // Open the ConfigActivity when the settings icon is clicked
                val intent = Intent(activity, ConfigActivity::class.java)
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val uid = auth.currentUser?.uid

        with(binding) {
            if (uid != null) {
                val userDocRef = db.collection("usuarios").document(uid)

                // Observables for user data fields
                var userCompleteName: String by observable("") { _, _, newUserCompleteName ->
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
                var userDni: String by observable("") { _, _, newUserDni ->
                    userDni.text = newUserDni
                }
                var photoUrl: String by observable("") { _, _, newPhotoUrl ->
                    // Load user's profile photo using Glide library
                    val firebasePhotoStart =
                        "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                    if (firebasePhotoStart in newPhotoUrl) {
                        Glide.with(binding.root).load(newPhotoUrl).into(profileProfilePick)
                    } else {
                        profileProfilePick.setImageResource(R.drawable.fitness_gym_logo)
                    }
                }

                // Listen for changes in user document
                userDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        // Retrieve user data from the snapshot and update the observables
                        val newUserFirstName = snapshot.getString("first_name") ?: ""
                        val newUserLastName = snapshot.getString("last_name") ?: ""
                        val newUserBirthdate = snapshot.getString("birthdate") ?: ""
                        val newUserEmail = snapshot.getString("email") ?: ""
                        val newUserPhone = snapshot.getString("phone") ?: ""
                        val newUserDni = snapshot.getString("dni") ?: ""
                        val newPhotoUrl = snapshot.getString("photo") ?: ""

                        userCompleteName = "$newUserFirstName $newUserLastName"
                        userBirthdate = Dates.showProperDate(newUserBirthdate)
                        userEmail = newUserEmail
                        userPhone = newUserPhone
                        userDni = newUserDni
                        photoUrl = newPhotoUrl
                    }
                }
            }

            deleteUserButton.setOnClickListener {
                context?.let { it1 ->
                    MaterialAlertDialogBuilder(it1)
                        .setTitle(R.string.delete_user)
                        .setMessage(R.string.delete_user_message)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            if (uid != null) {
                                // Delete user document from Firestore
                                db.collection("usuarios").document(uid).delete()
                            }

                            // Show a success message using Snackbar
                            Snackbar.make(root, R.string.user_successfully_deleted, Snackbar.LENGTH_LONG).show()

                            // Delete the user account from Firebase Authentication
                            auth.currentUser?.delete()?.addOnSuccessListener {
                                // Sign out the user and navigate to the LoginActivity
                                auth.signOut()
                                activity?.finish()
                                startActivity(Intent(context, LoginActivity::class.java))
                                return@addOnSuccessListener
                            }?.addOnFailureListener {
                                Snackbar.make(root, R.string.error, Snackbar.LENGTH_LONG).show()
                            }
                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }

            editUserButton.setOnClickListener {
                // Launch EditProfileActivity using the activity result launcher
                answer.launch(Intent(context, EditProfileActivity::class.java))
            }
        }
    }

    /**
     * Handle the result of EditProfileActivity.
     *
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            AppCompatActivity.RESULT_OK -> {
                // Show a success message when the profile is successfully edited
                Snackbar.make(binding.root, R.string.user_successfully_edited, Snackbar.LENGTH_LONG).show()
            }
            AppCompatActivity.RESULT_CANCELED -> {
                // Handle the case when the profile editing is canceled
            }
            else -> {
                // Handle other result codes if necessary
                Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() {

        }
    }
}
