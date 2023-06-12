package com.example.fitnessgym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.GroupLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class GroupAdapter(var groups: MutableList<Group>): RecyclerView.Adapter<GroupAdapter.GroupContainer>() {

    private val db = FirebaseFirestore.getInstance()

    inner class GroupContainer(private val layout: GroupLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        fun bindGroup(group: Group) {
            with (layout) {
                val userDocRef = db.collection("usuarios").document(group.docMonitor)
                var userName: String by observable("") { _, _, newUserName ->
                    instructor.text = newUserName
                }

                userDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val newUserName = snapshot.getString("first_name") ?: ""

                        userName = newUserName
                    }
                }

                if (group.photo != "") {
                    Glide.with(root).load(group.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                groupName.text = group.name

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