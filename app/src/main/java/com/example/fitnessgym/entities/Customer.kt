package com.example.fitnessgym.entities

import java.io.Serializable

data class Customer(
    val id : Int = 0,
    val docId: String = "",
    val name: String = "",
    val surname: String = "",
    val birthdate: String = "",
    val email: String = "",
    val phone: String = "",
    val postalCode: String = "",
    val photo: String = "",
    val inscription: String = "",
    val idGroup: String = "") : Serializable