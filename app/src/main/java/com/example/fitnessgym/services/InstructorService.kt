package com.example.fitnessgym.services

import com.example.fitnessgym.entities.Instructor
import com.google.firebase.firestore.FirebaseFirestore

class InstructorService {
    companion object {
        // Method to register or edit an instructor in the Firebase Firestore database
        fun registerOrEditInstructor(db: FirebaseFirestore, instructor: Instructor, token: String) {
            // Create a HashMap with the instructor data
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

            // Set the document with the instructor data in the "usuarios" collection
            db.collection("usuarios").document(instructor.uid).set(doc)
        }
    }
}
