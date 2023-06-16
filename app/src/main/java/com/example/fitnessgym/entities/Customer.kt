package com.example.fitnessgym.entities

import java.io.Serializable

data class Customer(
    val id: Long = 0, // Unique identifier for the customer
    val docId: String = "", // Document ID associated with the customer in the database
    val name: String = "", // Customer's first name
    val surname: String = "", // Customer's last name
    val birthdate: String = "", // Customer's birthdate
    val email: String = "", // Customer's email address
    val phone: String = "", // Customer's phone number
    val postal_code: String = "", // Customer's postal code
    val photo: String? = "", // URL or path to the customer's photo, nullable
    val inscription: String = "", // Date of customer's inscription
    var idgroup: String? = "" // Identifier of the group the customer belongs to, nullable
) : Serializable
