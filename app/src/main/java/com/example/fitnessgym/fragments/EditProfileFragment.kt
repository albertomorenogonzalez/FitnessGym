package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentEditProfileBinding
import com.fitness.fitnessgym.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val uid = auth.currentUser?.uid

        with (binding) {
            if (uid != null) {
                db.collection("usuarios").document(uid).get().addOnSuccessListener {
                    if (!it.get("photo")?.equals("")!!) {
                        Glide.with(binding.root.context).load(it.get("photo"))
                            .into(binding.profilePick)
                    }

                    txtProfileName.setText("${it.get("first_name")} ${it.get("last_name")}")
                    userBirthdate.text = it.get("birthdate").toString()
                    userEmail.text = it.get("email").toString()
                    userPhone.text = it.get("phone").toString()
                    userDni.text = it.get("dni").toString()
                }
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() {

        }

    }
}