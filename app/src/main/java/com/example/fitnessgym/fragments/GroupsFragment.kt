package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fitnessgym.activities.AddEditCustomerActivity
import com.example.fitnessgym.adapter.GroupAdapter
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentGroupsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.properties.Delegates.observable


class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var adapter: GroupAdapter
    private var form = ""
    private val db = FirebaseFirestore.getInstance()

    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)

        val groupList: MutableList<Group> by observable(mutableListOf()) { _, _, _ ->
            adapter.notifyDataSetChanged()
        }

        val groupsCollectionRef = db.collection("grupos")

        adapter = GroupAdapter(groupList)

        binding.groupsView.adapter = adapter
        binding.groupsView.setHasFixedSize(true)

        groupsCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            groupList.clear()

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val id = document.get("id") as Long
                    val docId = document.get("docId").toString()
                    val name = document.get("name").toString()
                    val docMonitor = document.get("docMonitor").toString()
                    val description = document.get("description").toString()
                    val photo = document.get("photo").toString()

                    val group = Group(id, docId, name, docMonitor, description, photo)

                    groupList.add(group)
                }
            }

            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.addButton.setOnClickListener {
            val i = Intent(context, AddEditCustomerActivity::class.java)
            form = "add"
            i.putExtra("form", form)
            answer.launch(i)
        }
    }

    /**
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            AppCompatActivity.RESULT_OK -> {

                val form = data.data?.getStringExtra("form")

                if (form == "add") {
                    Snackbar.make(binding.root, R.string.customer_successfully_added, Snackbar.LENGTH_LONG).show()
                } else if (form == "delete") {
                    Snackbar.make(binding.root, R.string.customer_successfully_deleted, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, R.string.customer_successfully_edited, Snackbar.LENGTH_LONG).show()
                }


            }
            AppCompatActivity.RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, "canceled", Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupsFragment()

    }
}
