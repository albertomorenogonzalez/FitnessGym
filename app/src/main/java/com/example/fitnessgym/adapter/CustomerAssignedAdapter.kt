package com.example.fitnessgym.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.CustomerAssignedLayoutBinding
import com.fitness.fitnessgym.databinding.CustomerLayoutBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

class CustomerAssignedAdapter(var customers: MutableList<Customer>): RecyclerView.Adapter<CustomerAssignedAdapter.CustomerAssignedContainer>() {

    inner class CustomerAssignedContainer(private val layout: CustomerAssignedLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        @SuppressLint("SetTextI18n")
        fun bindCustomer(customer: Customer) {
            with (layout) {
                if (customer.photo != "") {
                    Glide.with(root).load(customer.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

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
        holder.bindCustomer((customers[position]))
    }

    override fun getItemCount(): Int = customers.size
}
