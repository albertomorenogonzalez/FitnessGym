package com.example.fitnessgym.entities

import java.io.Serializable

data class Customer(
    val id : Long = 0,
    val docId: String = "",
    val name: String = "",
    val surname: String = "",
    val birthdate: String = "",
    val email: String = "",
    val phone: String = "",
    val postal_code: String = "",
    val photo: String? = "",
    val inscription: String = "",
    val idgroup: String? = "") : Serializable