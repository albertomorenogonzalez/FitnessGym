package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.GroupLayoutBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.properties.Delegates.observable

class GroupAdapter(var groups: MutableList<Group>,
                   val groupLongClick: (MenuItem, Group) -> Boolean): RecyclerView.Adapter<GroupAdapter.GroupContainer>() {

    private val db = FirebaseFirestore.getInstance()

    inner class GroupContainer(private val layout: GroupLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        fun bindGroup(group: Group) {
            with (layout) {
                val userDocRef = db.collection("usuarios").document(group.docMonitor)
                var userName: String by observable("") { _, _, newUserName ->
                    instructor.text = root.context.getString(R.string.instructor, newUserName)
                }

                userDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val newUserName = snapshot.getString("first_name") ?: ""
                        val newUserSurname = snapshot.getString("last_name") ?: ""

                        userName = "$newUserName $newUserSurname"
                    }
                }

                if (group.photo != "") {
                    Glide.with(root).load(group.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                groupName.text = group.name

                root.setOnLongClickListener {
                    val menu = PopupMenu(root.context, groupName)

                    menu.inflate(R.menu.group_options)

                    menu.setOnMenuItemClickListener {
                        groupLongClick(it, group)
                    }

                    menu.show()

                    true
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupContainer {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GroupLayoutBinding.inflate(inflater)

        return GroupContainer(binding)
    }

    override fun onBindViewHolder(holder: GroupContainer, position: Int) {
        holder.bindGroup((groups[position]))
    }

    override fun getItemCount(): Int = groups.size
}