package com.example.fitnessgym.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitnessgym.adapter.GroupAssignAdapter
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityAddChangeGroupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates.observable

class AddChangeGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChangeGroupBinding
    private lateinit var adapter: GroupAssignAdapter
    private val db = FirebaseFirestore.getInstance()

    // Method called when the activity is created
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChangeGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the background color of the action bar to red
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red)))

        // Reference to the "grupos" collection in Firestore
        val groupsCollectionRef = db.collection("grupos")

        // Mutable list of groups
        val groupList: MutableList<Group> by observable(mutableListOf()) { _, _, _ ->
            adapter.notifyDataSetChanged()
        }

        // Get the selected customer ID from the Intent extras
        val uid = intent.extras?.getString("customer_uid")
        val customerRef = db.collection("clientes").document(uid.toString())

        // Function to execute when adding the customer to a group
        val addToGroupButton: (String, String) -> Unit = { idgroup, groupName ->
            customerRef.update("idgroup", idgroup)
                .addOnSuccessListener {
                    val i = Intent()
                    val extras = Bundle().apply {
                        putString("group", "group")
                        putString("group_name", groupName)
                    }
                    i.putExtras(extras)
                    setResult(Activity.RESULT_OK, i)
                    finish()
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root, R.string.error_adding_id, Snackbar.LENGTH_SHORT).show()
                }
        }

        // Initialize the group adapter with the group list and the addToGroupButton function
        adapter = GroupAssignAdapter(groupList, addToGroupButton)

        // Configure the adapter and set the fixed size for the RecyclerView in the layout
        binding.addEditGroupView.adapter = adapter
        binding.addEditGroupView.setHasFixedSize(true)

        // Listen for changes in the "grupos" collection in Firestore
        groupsCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            // Clear the current group list
            groupList.clear()

            if (snapshot != null) {
                // Iterate through the documents in the snapshot
                for (document in snapshot.documents) {
                    // Get the fields of the document
                    val id = document.get("id") as Long
                    val docId = document.id
                    val name = document.get("name").toString()
                    val docMonitor = document.get("docMonitor").toString()
                    val description = document.get("description").toString()
                    val photo = document.get("photo").toString()

                    // Create a Group object with the document data and add it to the list
                    val group = Group(id, docId, name, docMonitor, description, photo)
                    groupList.add(group)
                }
            }

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged()
        }
    }
}
