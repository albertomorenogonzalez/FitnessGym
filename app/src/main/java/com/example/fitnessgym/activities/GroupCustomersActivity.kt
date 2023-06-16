package com.example.fitnessgym.activities

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.fitnessgym.adapter.CustomerAdapter
import com.example.fitnessgym.adapter.CustomerAssignedAdapter
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.entities.Group
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityGroupCustomersBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.Delegates.observable

@SuppressLint("NotifyDataSetChanged")
class GroupCustomersActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGroupCustomersBinding
    private lateinit var adapter: CustomerAssignedAdapter
    private val db = FirebaseFirestore.getInstance()

    private val customerList: MutableList<Customer> by observable(mutableListOf()) { _, _, _ ->
        adapter.notifyDataSetChanged()
    }

    private val customersCollectionRef = db.collection("clientes")

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = intent.extras?.get("group_name").toString()
        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(
            ColorDrawable(resources.getColor(
                R.color.red))
        ))

        binding = ActivityGroupCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CustomerAssignedAdapter(customerList)

        binding.customersView.adapter = adapter
        binding.customersView.setHasFixedSize(true)

        customersCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("CustomersFragment", "Error listening for customer changes: ${error.message}")
                return@addSnapshotListener
            }

            customerList.clear()

            if (snapshot != null) {
                val customerGroup = intent.extras?.get("group_uid")

                for (document in snapshot.documents) {

                    if (document.get("idgroup").toString() == customerGroup) {
                        val id = document.get("id") as Long
                        val uid = document.id
                        val name = document.get("name").toString()
                        val surname = document.get("surname").toString()
                        val birthdate = document.get("birthdate").toString()
                        val email = document.get("email").toString()
                        val phone = document.get("phone").toString()
                        val postalCode = document.get("postal_code").toString()
                        var photo = ""
                        if (document.get("photo") != null) {
                            photo = document.get("photo").toString()
                        }
                        val inscription = document.get("inscription").toString()
                        val idGroup = document.get("idgroup").toString()

                        val customer = Customer(id, uid, name, surname, birthdate, email, phone, postalCode, photo, inscription, idGroup)

                        customerList.add(customer)
                    }
                }

                adapter.notifyDataSetChanged()

            }
        }

        binding.backButton.setOnClickListener {
            finish()
            return@setOnClickListener
        }
    }
}