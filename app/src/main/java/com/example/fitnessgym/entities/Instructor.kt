package com.example.fitnessgym.entities

import java.io.Serializable

data class Instructor(
    val uid : String = "",
    val first_name : String = "",
    val last_name : String = "",
    val birthdate : String = "",
    val email : String = "",
    val phone : String = "",
    val dni : String = "",
    val photo : String? = "") : Serializable
