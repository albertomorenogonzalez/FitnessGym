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
import com.fitness.fitnessgym.databinding.CustomerLayoutBinding

class CustomerAdapter(var customers: MutableList<Customer>,
                      val customerLongClick: (MenuItem, Customer) -> Boolean): RecyclerView.Adapter<CustomerAdapter.CustomerContainer>() {
    inner class CustomerContainer(private val layout: CustomerLayoutBinding): RecyclerView.ViewHolder(layout.root) {
        @SuppressLint("SetTextI18n")
        fun bindCustomer(customer: Customer) {
            with (layout) {
                if (customer.photo != "") {
                    Glide.with(root).load(customer.photo).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

                name.text = "${customer.name} ${customer.surname}"
                email.text = customer.email

                val inscriptionDateDate = Dates.showProperDate(customer.inscription)

                inscriptionDate.text = root.context.getString(R.string.inscription_date, inscriptionDateDate)


                root.setOnLongClickListener {
                    val menu = PopupMenu(root.context, name)

                    menu.inflate(R.menu.customer_options)

                    menu.setOnMenuItemClickListener {
                        customerLongClick(it, customer)
                    }

                    menu.show()

                    true
                }
            }

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
