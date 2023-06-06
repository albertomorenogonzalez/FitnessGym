package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.databinding.InstructorLayoutBinding

class InstructorAdapter(var instructors: MutableList<Instructor>): RecyclerView.Adapter<InstructorAdapter.InstructorsContainer>() {

    inner class InstructorsContainer(val layout: InstructorLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        fun bindInstructor(instructor: Instructor) {
         with (layout) {
            Glide.with(root).load(instructor.photo).into(profilePick)
            name.text = instructor.first_name
            surname.text = instructor.last_name
            email.text = instructor.email
         }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorsContainer {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InstructorLayoutBinding.inflate(inflater)

        return InstructorsContainer(binding)
    }

    override fun onBindViewHolder(holder: InstructorsContainer, position: Int) {
        holder.bindInstructor(instructors[position])
    }

    override fun getItemCount(): Int = instructors.size
}