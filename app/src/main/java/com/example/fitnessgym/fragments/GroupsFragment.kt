package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessgym.activities.AddEditGroupActivity
import com.example.fitnessgym.activities.GroupCustomersActivity
import com.example.fitnessgym.activities.GroupViewActivity
import com.example.fitnessgym.activities.InstructorViewActivity
import com.example.fitnessgym.adapter.GroupAdapter
import com.example.fitnessgym.entities.Group
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentGroupsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
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

        val groupLongClick: (MenuItem, Group) -> Boolean = { item, group ->

            when (item.itemId) {
                R.id.view_group -> {
                    val i = Intent(context, GroupViewActivity::class.java)
                    i.putExtra("group", group)
                    answer.launch(i)
                }
                R.id.group_customers -> {
                    val i = Intent(context, GroupCustomersActivity::class.java)
                    i.putExtra("group_uid", group.docId)
                    i.putExtra("group_name", group.name)
                    answer.launch(i)
                }
                R.id.edit_group -> {
                    val i = Intent(context, AddEditGroupActivity::class.java)
                    form = "edit"
                    i.putExtra("form", form)
                    i.putExtra("group", group)
                    answer.launch(i)
                }
                R.id.delete_group -> {
                    context?.let { it1 ->
                        MaterialAlertDialogBuilder(it1)
                            .setTitle(R.string.delete_user)
                            .setMessage(R.string.delete_group_message)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                db.collection("grupos").document(group.docId).delete()

                                Snackbar.make(requireView(), R.string.group_successfully_deleted, Snackbar.LENGTH_LONG).show()
                            }
                            .setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }

            true

        }

        adapter = GroupAdapter(groupList, groupLongClick)

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.addButton.setOnClickListener {
            val i = Intent(context, AddEditGroupActivity::class.java)
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
                Snackbar.make(binding.root, R.string.operation_completed, Snackbar.LENGTH_LONG).show()
            }
            AppCompatActivity.RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupsFragment()

    }
}
