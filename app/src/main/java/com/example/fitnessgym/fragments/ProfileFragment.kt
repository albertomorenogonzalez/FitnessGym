package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fitnessgym.activities.ConfigActivity
import com.example.fitnessgym.activities.EditProfileActivity
import com.example.fitnessgym.activities.LoginActivity
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
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_button -> {
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

        with (binding) {
            if (uid != null) {
                val userDocRef = db.collection("usuarios").document(uid)
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
                        val newUserDni = snapshot.getString("dni") ?: ""
                        val newPhotoUrl = snapshot.getString("photo") ?: ""

                        userCompleteName = "$newUserFirstName $newUserLastName"
                        userBirthdate = newUserBirthdate
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
                                db.collection("usuarios").document(uid).delete()
                            }

                            Snackbar.make(root, "User deleted", Snackbar.LENGTH_LONG).show()

                            auth.currentUser?.delete()?.addOnSuccessListener {
                                auth.signOut()
                                activity?.finish()
                                startActivity(Intent(context, LoginActivity::class.java))
                                return@addOnSuccessListener
                            }?.addOnFailureListener {
                                Snackbar.make(root, "Something went wrong", Snackbar.LENGTH_LONG).show()
                            }


                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }


            }


            editUserButton.setOnClickListener {
                answer.launch(Intent(context, EditProfileActivity::class.java))
            }
        }
    }

    /**
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            AppCompatActivity.RESULT_OK -> {
                Snackbar.make(binding.root, R.string.user_successfully_edited, Snackbar.LENGTH_LONG).show()
            }
            AppCompatActivity.RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, "canceled", Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() {

        }

    }
}