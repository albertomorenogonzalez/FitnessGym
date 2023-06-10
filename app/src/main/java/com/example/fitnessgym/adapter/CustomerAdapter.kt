package com.example.fitnessgym.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessgym.entities.Customer
import com.fitness.fitnessgym.databinding.CustomerLayoutBinding

class CustomerAdapter(var customers: MutableList<Customer>): RecyclerView.Adapter<CustomerAdapter.CustomerContainer>() {
    inner class CustomerContainer(private val layout: CustomerLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        fun bindCustomer(customer: Customer) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerContainer {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CustomerLayoutBinding.inflate(inflater)

        return CustomerContainer(binding)
    }

    override fun onBindViewHolder(holder: CustomerContainer, position: Int) {
        holder.bindCustomer((customers[position]))
    }

    override fun getItemCount(): Int = customers.size
}