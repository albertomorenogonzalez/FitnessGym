package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.InstructorLayoutBinding

class InstructorAdapter(var instructors: MutableList<Instructor>): RecyclerView.Adapter<InstructorAdapter.InstructorsContainer>() {

    inner class InstructorsContainer(private val layout: InstructorLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        @SuppressLint("SetTextI18n")
        fun bindInstructor(instructor: Instructor) {
         with (layout) {
             if (instructor.photo != "") {
                 Glide.with(root).load(instructor.photo).into(profilePick)
             } else {
                 profilePick.setImageResource(R.drawable.fitness_gym_logo)
             }

             name.text = "${instructor.first_name} ${instructor.last_name}"
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