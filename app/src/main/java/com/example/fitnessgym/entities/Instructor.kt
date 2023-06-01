package com.example.fitnessgym.entities

import java.io.Serializable

data class Instructor(
    val insId : Long? = null,
    val docId : String = "",
    val insName : String = "",
    val insSur : String = "",
    val insBd : String = "",
    val insEmail : String = "",
    val insPw : String = "",
    val insTelNum : String = "",
    val insDni : String = "",
    val insPhoto : String = "") : Serializable
