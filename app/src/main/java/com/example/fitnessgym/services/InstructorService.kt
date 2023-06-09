package com.example.fitnessgym.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.fitnessgym.entities.Instructor
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class InstructorService {
    companion object {
        fun registerOrEditInstructor(db: FirebaseFirestore, instructor: Instructor, token: String) {
            val doc = hashMapOf<String, Any>(
                "uid" to instructor.uid,
                "first_name" to instructor.first_name,
                "last_name" to instructor.last_name,
                "birthdate" to instructor.birthdate,
                "email" to instructor.email,
                "phone" to instructor.phone,
                "dni" to instructor.dni,
                "photo" to instructor.photo!!,
                "provider" to "firebase",
                "token" to token
            )

            db.collection("usuarios").document(instructor.uid).set(doc)
        }
    }

}