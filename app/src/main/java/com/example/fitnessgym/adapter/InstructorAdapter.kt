package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Instructor
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.InstructorLayoutBinding

class InstructorAdapter(var instructors: MutableList<Instructor>,
                        val instructorLongClick: (MenuItem, Instructor) -> Boolean): RecyclerView.Adapter<InstructorAdapter.InstructorsContainer>() {

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

                 root.setOnLongClickListener {
                     val menu = PopupMenu(root.context, name)

                     menu.inflate(R.menu.instructor_options)

                     menu.setOnMenuItemClickListener {
                         instructorLongClick(it, instructor)
                     }

                     menu.show()

                     true
                 }
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