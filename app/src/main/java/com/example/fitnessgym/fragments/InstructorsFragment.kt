package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.fitnessgym.adapter.InstructorAdapter
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates.observable

class InstructorsFragment : Fragment() {

    private lateinit var binding: FragmentInstructorsBinding
    private lateinit var adapter: InstructorAdapter
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructorsBinding.inflate(inflater, container, false)

        val userList: MutableList<Instructor> by observable(mutableListOf()) { _, _, _ ->
            adapter.notifyDataSetChanged()
        }

        val usersCollectionRef = db.collection("usuarios")

        adapter = InstructorAdapter(userList)

        binding.instructorsView.adapter = adapter
        binding.instructorsView.setHasFixedSize(true)

        usersCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                return@addSnapshotListener
            }

            userList.clear()

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val uid = document.get("uid").toString()
                    val firstName = document.get("first_name").toString()
                    val lastName = document.get("last_name").toString()
                    val birthdate = document.get("birthdate").toString()
                    val email = document.get("email").toString()
                    val phone = document.get("phone").toString()
                    val dni = document.get("dni").toString()
                    val photo = document.get("photo").toString()

                    val user = Instructor(uid, firstName, lastName, birthdate, email, phone, dni, photo)

                    userList.add(user)
                }
            }

            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = InstructorsFragment()
    }
}
