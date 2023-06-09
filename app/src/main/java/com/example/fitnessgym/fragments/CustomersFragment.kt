package com.example.fitnessgym.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessgym.activities.*
import com.example.fitnessgym.adapter.CustomerAdapter
import com.example.fitnessgym.entities.Customer
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentCustomersBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates.observable

@SuppressLint("NotifyDataSetChanged")
class CustomersFragment : Fragment() {

    private lateinit var binding: FragmentCustomersBinding
    private lateinit var adapter: CustomerAdapter
    private var form = ""
    private val db = FirebaseFirestore.getInstance()

    // ActivityResult launcher for handling results from other activities
    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    // MutableList of Customer objects with observable property delegate
    private val customerList: MutableList<Customer> by observable(mutableListOf()) { _, _, _ ->
        adapter.notifyDataSetChanged()
    }

    private val customersCollectionRef = db.collection("clientes")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomersBinding.inflate(inflater, container, false)

        // Long click listener for options menu items
        val customerLongClick: (MenuItem, Customer) -> Boolean = { item, customer ->
            when (item.itemId) {
                R.id.view_customer -> {
                    // Launch CustomerViewActivity to view customer details
                    val i = Intent(context, CustomerViewActivity::class.java)
                    i.putExtra("customer", customer)
                    answer.launch(i)
                }
                R.id.edit_customer -> {
                    // Launch AddEditCustomerActivity to edit customer details
                    val i = Intent(context, AddEditCustomerActivity::class.java)
                    form = "edit"
                    i.putExtra("form", form)
                    i.putExtra("customer", customer)
                    answer.launch(i)
                }
                R.id.add_customer -> {
                    if (customer.idgroup == "") {
                        // Launch AddChangeGroupActivity to assign a group to the customer
                        val i = Intent(context, AddChangeGroupActivity::class.java)
                        i.putExtra("customer_uid", customer.docId)
                        answer.launch(i)
                    } else {
                        Snackbar.make(binding.root, R.string.customer_in_group, Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.delete_customer -> {
                    // Show a confirmation dialog before deleting the customer
                    context?.let { it1 ->
                        MaterialAlertDialogBuilder(it1)
                            .setTitle(R.string.delete_user)
                            .setMessage(R.string.delete_customer_message)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                // Delete the customer from the Firestore collection
                                db.collection("clientes").document(customer.docId).delete()

                                Snackbar.make(requireView(), R.string.customer_successfully_deleted, Snackbar.LENGTH_LONG).show()
                            }
                            .setNegativeButton(R.string.no) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }

            false
        }

        adapter = CustomerAdapter(customerList, customerLongClick)

        binding.customersView.adapter = adapter
        binding.customersView.setHasFixedSize(true)

        // Listen for changes in the customers collection and update the customerList accordingly
        customersCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("CustomersFragment", "Error listening for customer changes: ${error.message}")
                return@addSnapshotListener
            }

            customerList.clear()

            if (snapshot != null) {
                for (document in snapshot.documents) {
                    // Extract customer data from the document
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

                    // Create a Customer object with the extracted data and add it to the customerList
                    val customer = Customer(id, uid, name, surname, birthdate, email, phone, postalCode, photo, inscription, idGroup)
                    customerList.add(customer)
                }
            }

            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Set a click listener for the add button to launch AddEditCustomerActivity
        binding.addButton.setOnClickListener {
            val i = Intent(context, AddEditCustomerActivity::class.java)
            form = "add"
            i.putExtra("form", form)
            answer.launch(i)
        }
    }

    /**
     * Function to handle the result of the activity launched with ActivityResultContracts.StartActivityForResult
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            AppCompatActivity.RESULT_OK -> {
                val form = data.data?.getStringExtra("form")
                val group = data.data?.getStringExtra("group")
                val groupName = data.data?.getStringExtra("group_name")

                if (form == "add") {
                    Snackbar.make(binding.root, R.string.customer_successfully_added, Snackbar.LENGTH_LONG).show()
                } else if (form == "delete") {
                    Snackbar.make(binding.root, R.string.customer_successfully_deleted, Snackbar.LENGTH_LONG).show()
                } else if (group == "group") {
                    Snackbar.make(binding.root, getString(R.string.customer_in_a_group, groupName), Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, R.string.customer_successfully_edited, Snackbar.LENGTH_LONG).show()
                }
            }
            AppCompatActivity.RESULT_CANCELED -> {
                // Handle canceled result if needed
            }
            else -> {
                Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CustomersFragment()
    }
}
