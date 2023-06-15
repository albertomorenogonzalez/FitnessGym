package com.example.fitnessgym.entities

import java.io.Serializable

data class Group(
    val id : Long = 0,
    val docId: String = "",
    val name: String = "",
    val docMonitor: String = "",
    val description: String = "",
    val photo: String? = "",) : Serializable
