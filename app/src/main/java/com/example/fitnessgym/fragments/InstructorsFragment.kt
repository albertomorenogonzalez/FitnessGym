package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.fitnessgym.activities.AddChangeGroupActivity
import com.example.fitnessgym.activities.AddEditCustomerActivity
import com.example.fitnessgym.activities.CustomerViewActivity
import com.example.fitnessgym.activities.InstructorViewActivity
import com.example.fitnessgym.adapter.InstructorAdapter
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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

        val instructorLongClick: (MenuItem, Instructor) -> Boolean = { item, instructor ->

            when (item.itemId) {
                R.id.view_instructor -> {
                    val i = Intent(context, InstructorViewActivity::class.java)
                    i.putExtra("instructor", instructor)
                    startActivity(i)
                }
            }

            true

        }

        adapter = InstructorAdapter(userList, instructorLongClick)

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
