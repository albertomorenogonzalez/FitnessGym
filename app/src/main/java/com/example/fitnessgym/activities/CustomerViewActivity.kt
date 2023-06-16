package com.example.fitnessgym.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.fitnessgym.entities.Customer
import com.example.fitnessgym.functions.Dates
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityCustomerViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlin.properties.Delegates.observable

class CustomerViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerViewBinding
    private val db = FirebaseFirestore.getInstance()
    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityCustomerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        var customer = intent.extras?.get("customer") as Customer

        val customerDocRef = db.collection("clientes").document(customer.docId)

        with(binding) {
            // Define observable properties for customer details
            var customerCompleteName: String by observable("") { _, _, newCustomerCompleteName ->
                customerName.text = newCustomerCompleteName
            }
            var customerEmail: String by observable("") { _, _, newCustomerEmail ->
                email.text = newCustomerEmail
            }
            var customerBirthdate: String by observable("") { _, _, newCustomerBirthdate ->
                birthdate.text = newCustomerBirthdate
            }
            var customerPhone: String by observable("") { _, _, newCustomerPhone ->
                phone.text = newCustomerPhone
            }
            var customerPostalCode: String by observable("") { _, _, newCustomerPostalCode ->
                postalCode.text = newCustomerPostalCode
            }
            var customerInscriptionDate: String by observable("") { _, _, newCustomerInsDate ->
                inscriptionDate.text = newCustomerInsDate
            }

            var groupName: String by observable("") { _, _, newGroupName ->
                group.text = newGroupName
            }

            // Check if the customer is in a group or not
            if (customer.idgroup != "") {
                // Customer is in a group
                deleteCustomerButtonAdg.visibility = View.GONE
                addGroupButton.visibility = View.GONE
                editCustomerButton.visibility = View.VISIBLE

                // Retrieve the group details from Firestore
                val groupDocRef = db.collection("grupos").document(customer.idgroup.toString())
                groupDocRef.get()
                    .addOnSuccessListener { groupSnapshot ->
                        if (groupSnapshot != null && groupSnapshot.exists()) {
                            val newGroupName = groupSnapshot.getString("name") ?: ""
                            groupName = newGroupName
                        }
                    }
            } else {
                // Customer is not in a group
                deleteCustomerButtonEdg.visibility = View.GONE
                quitGroupButton.visibility = View.GONE
                group.visibility = View.GONE
                changeGroupButton.visibility = View.GONE
            }

            var photoUrl: String by observable("") { _, _, newPhotoUrl ->
                if (!isDestroyed) {
                    // Load the customer's photo using Glide
                    val firebasePhotoStart =
                        "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                    if (firebasePhotoStart in newPhotoUrl) {
                        Glide.with(binding.root).load(newPhotoUrl).into(customerCustomerPick)
                    } else {
                        customerCustomerPick.setImageResource(R.drawable.fitness_gym_logo)
                    }
                }
            }

            // Listen for changes in the customer document in Firestore
            customerDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Retrieve customer details from the snapshot
                    val newCustomerFirstName = snapshot.getString("name") ?: ""
                    val newCustomerLastName = snapshot.getString("surname") ?: ""
                    val newCustomerBirthdate = snapshot.getString("birthdate") ?: ""
                    val newCustomerEmail = snapshot.getString("email") ?: ""
                    val newCustomerPhone = snapshot.getString("phone") ?: ""
                    val newCustomerPostalCode = snapshot.getString("postal_code") ?: ""
                    val newCustomerInscriptionDate = snapshot.getString("inscription") ?: ""
                    val newPhotoUrl = snapshot.getString("photo") ?: ""

                    // Update the customer details in the UI
                    customerCompleteName = "$newCustomerFirstName $newCustomerLastName"
                    supportActionBar?.title = customerCompleteName
                    customerEmail = newCustomerEmail
                    customerBirthdate = Dates.showProperDate(newCustomerBirthdate)
                    customerPhone = newCustomerPhone
                    customerPostalCode = newCustomerPostalCode
                    customerInscriptionDate = Dates.showProperDate(newCustomerInscriptionDate)
                    photoUrl = newPhotoUrl

                    if (customer.idgroup != "") {
                        // Retrieve the group details if the customer is in a group
                        val groupDocRef = db.collection("grupos").document(customer.idgroup.toString())
                        groupDocRef.get()
                            .addOnSuccessListener { groupSnapshot ->
                                if (groupSnapshot != null && groupSnapshot.exists()) {
                                    val newGroupName = groupSnapshot.getString("name") ?: ""
                                    groupName = newGroupName
                                }
                            }
                    }

                    // Update the customer object with the latest details
                    customer = Customer(0,
                        customer.docId,
                        newCustomerFirstName,
                        newCustomerLastName,
                        newCustomerBirthdate,
                        newCustomerEmail,
                        newCustomerPhone,
                        newCustomerPostalCode,
                        newPhotoUrl,
                        newCustomerInscriptionDate,
                        customer.idgroup)
                }
            }

            // Add group button click listener
            addGroupButton.setOnClickListener {
                val i = Intent(this@CustomerViewActivity, AddChangeGroupActivity::class.java)
                i.putExtra("customer_uid", customer.docId)
                answer.launch(i)
            }

            // Edit customer button click listener
            editCustomerButton.setOnClickListener {
                val i = Intent(this@CustomerViewActivity, AddEditCustomerActivity::class.java)
                i.putExtra("customer", customer)
                answer.launch(i)
            }

            // Quit group button click listener
            quitGroupButton.setOnClickListener {
                customerDocRef.update("idgroup", "")
                    .addOnSuccessListener {
                        // Update the customer locally
                        customer.idgroup = ""

                        // Update the visibility of buttons and views
                        with(binding) {
                            deleteCustomerButtonAdg.visibility = View.VISIBLE
                            addGroupButton.visibility = View.VISIBLE
                            editCustomerButton.visibility = View.VISIBLE
                            deleteCustomerButtonEdg.visibility = View.GONE
                            quitGroupButton.visibility = View.GONE
                            group.visibility = View.GONE
                            changeGroupButton.visibility = View.GONE
                        }

                        // Show a success message
                        Snackbar.make(binding.root, getString(R.string.customer_removed, customer.name), Snackbar.LENGTH_LONG).show()
                    }
            }

            // Change group button click listener
            changeGroupButton.setOnClickListener {
                val i = Intent(this@CustomerViewActivity, AddChangeGroupActivity::class.java)
                i.putExtra("customer_uid", customer.docId)
                answer.launch(i)
            }

            // Delete customer button click listener (for customers in a group)
            deleteCustomerButtonEdg.setOnClickListener {
                deleteCustomer(customer.docId)
            }

            // Delete customer button click listener (for customers not in a group)
            deleteCustomerButtonAdg.setOnClickListener {
                deleteCustomer(customer.docId)
            }

            // Back button click listener
            viewBackButton.setOnClickListener {
                finish()
                return@setOnClickListener
            }
        }
    }

    // Function to delete a customer
    private fun deleteCustomer(docId: String) {
        MaterialAlertDialogBuilder(this@CustomerViewActivity)
            .setTitle(R.string.delete_user)
            .setMessage(R.string.delete_customer_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                db.collection("clientes").document(docId).delete()

                val i = Intent()
                i.putExtra("form", "delete")
                setResult(RESULT_OK, i)
                finish()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Function to handle the result of the activity launched for adding or changing a group
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            RESULT_OK -> {
                val group = data.data?.getStringExtra("group")
                val groupName = data.data?.getStringExtra("group_name")

                if (group == "group") {
                    // The customer was added to a group
                    with (binding) {
                        deleteCustomerButtonAdg.visibility = View.GONE
                        addGroupButton.visibility = View.GONE
                        editCustomerButton.visibility = View.VISIBLE
                        deleteCustomerButtonEdg.visibility = View.VISIBLE
                        quitGroupButton.visibility = View.VISIBLE
                        binding.group.visibility = View.VISIBLE
                        binding.group.text = groupName
                        changeGroupButton.visibility = View.VISIBLE
                    }

                    Snackbar.make(binding.root, getString(R.string.customer_in_a_group), Snackbar.LENGTH_LONG).show()
                } else {
                    // The customer details were successfully edited
                    Snackbar.make(binding.root, R.string.customer_successfully_edited, Snackbar.LENGTH_LONG).show()
                }
            }
            RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }
}
