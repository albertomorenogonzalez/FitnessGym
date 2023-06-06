package com.example.fitnessgym.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityConfigBinding
import com.fitness.fitnessgym.databinding.ActivityLoginBinding

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}