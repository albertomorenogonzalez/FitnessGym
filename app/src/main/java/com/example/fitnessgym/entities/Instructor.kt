package com.example.fitnessgym.entities

import java.io.Serializable

data class Instructor(
    val uid: String = "", // Unique identifier for the instructor
    val first_name: String = "", // First name of the instructor
    val last_name: String = "", // Last name of the instructor
    val birthdate: String = "", // Birthdate of the instructor
    val email: String = "", // Email of the instructor
    val phone: String = "", // Phone number of the instructor
    val dni: String = "", // DNI (National Identity Document) of the instructor
    val photo: String? = "" // URL or path to the instructor's photo, nullable
) : Serializable
