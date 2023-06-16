package com.example.fitnessgym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.GroupAssignLayoutBinding

class GroupAssignAdapter(
    var groups: MutableList<Group>,
    val addToGroup: (String, String) -> Unit
) : RecyclerView.Adapter<GroupAssignAdapter.GroupAssignContainer>() {

    inner class GroupAssignContainer(private val layout: GroupAssignLayoutBinding) : RecyclerView.ViewHolder(layout.root) {

        fun bindGroup(group: Group) {
            with(layout) {
                // Load group's photo using Glide library if available, otherwise use a default image
                if (group.photo != "") {
                    Glide.with(root).load(group.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                // Set group's name
                groupName.text = group.name

                // Set a click listener for the "Add to Group" button
                addToGroupButton.setOnClickListener {
                    addToGroup(group.docId, group.name)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAssignContainer {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GroupAssignLayoutBinding.inflate(inflater)

        return GroupAssignContainer(binding)
    }

    override fun onBindViewHolder(holder: GroupAssignContainer, position: Int) {
        holder.bindGroup(groups[position])
    }

    override fun getItemCount(): Int = groups.size
}

