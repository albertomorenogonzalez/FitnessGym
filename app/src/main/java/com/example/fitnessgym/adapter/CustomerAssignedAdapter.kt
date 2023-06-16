package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.CustomerAssignedLayoutBinding

class CustomerAssignedAdapter(var customers: MutableList<Customer>) : RecyclerView.Adapter<CustomerAssignedAdapter.CustomerAssignedContainer>() {

    inner class CustomerAssignedContainer(private val layout: CustomerAssignedLayoutBinding) : RecyclerView.ViewHolder(layout.root) {

        @SuppressLint("SetTextI18n")
        fun bindCustomer(customer: Customer) {
            with(layout) {
                // Load customer's photo using Glide library if available, otherwise use a default image
                if (customer.photo != "") {
                    Glide.with(root).load(customer.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                // Set customer's name and surname
                name.text = "${customer.name} ${customer.surname}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerAssignedContainer {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CustomerAssignedLayoutBinding.inflate(inflater)

        return CustomerAssignedContainer(binding)
    }

    override fun onBindViewHolder(holder: CustomerAssignedContainer, position: Int) {
        holder.bindCustomer(customers[position])
    }

    override fun getItemCount(): Int = customers.size
}
