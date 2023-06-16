package com.example.fitnessgym.entities

import java.io.Serializable

data class Group(
    val id: Long = 0, // Unique identifier for the group
    val docId: String = "", // Document ID associated with the group in the database
    val name: String = "", // Group name
    val docMonitor: String = "", // Document ID of the group monitor/instructor
    val description: String = "", // Description of the group
    val photo: String? = "" // URL or path to the group's photo, nullable
) : Serializable
