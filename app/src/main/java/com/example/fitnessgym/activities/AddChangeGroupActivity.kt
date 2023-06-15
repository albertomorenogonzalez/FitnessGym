package com.example.fitnessgym.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.fitnessgym.adapter.GroupAdapter
import com.example.fitnessgym.adapter.GroupAssignAdapter
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.databinding.ActivityAddChangeGroupBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class AddChangeGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddChangeGroupBinding
    private lateinit var adapter: GroupAssignAdapter
    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddChangeGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val groupsCollectionRef = db.collection("grupos")

        val groupList: MutableList<Group> by observable(mutableListOf()) { _, _, _ ->
            adapter.notifyDataSetChanged()
        }

        val uid = intent.extras?.getString("customer_uid")
        val customerRef = db.collection("clientes").document(uid.toString())

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
                .addOnFailureListener { e ->
                    Log.e("AAA", "Error al añadir el ID del grupo al Customer", e)
                }
        }

        adapter = GroupAssignAdapter(groupList, addToGroupButton)

        binding.addEditGroupView.adapter = adapter
        binding.addEditGroupView.setHasFixedSize(true)

        groupsCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            groupList.clear()

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    val id = document.get("id") as Long
                    val docId = document.id
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
    }
}
