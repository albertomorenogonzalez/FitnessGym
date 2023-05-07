package com.example.fitnessgym.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}