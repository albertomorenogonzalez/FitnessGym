package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.fitnessgym.activities.ConfigActivity
import com.example.fitnessgym.activities.LoginActivity
import com.example.fitnessgym.activities.MainActivity
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding
import com.fitness.fitnessgym.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val configButton: ImageView? = view.findViewById(R.id.config_button)

        configButton?.setOnClickListener {
            val intent = Intent(activity, ConfigActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val uid = auth.currentUser?.uid

        with (binding) {
            if (uid != null) {
                db.collection("usuarios").document(uid).get().addOnSuccessListener {
                    if (!it.get("photo")?.equals("")!!) {
                        Glide.with(binding.root.context).load(it.get("photo")).into(binding.profileProfilePick)
                    }

                    profileName.text = "${it.get("first_name")} ${it.get("last_name")}"
                    userBirthdate.text = it.get("birthdate").toString()
                    userEmail.text = it.get("email").toString()
                    userPhone.text = it.get("phone").toString()
                    userDni.text = it.get("dni").toString()
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
                                Snackbar.make(root, "Algo falló", Snackbar.LENGTH_LONG).show()
                            }


                        }
                        .setNegativeButton(R.string.no) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }


            }



            editUserButton.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, EditProfileFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() {

        }

    }
}