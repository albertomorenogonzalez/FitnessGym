package com.example.fitnessgym.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fitness.fitnessgym.databinding.ActivityAddEditGroupBinding

class AddEditGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}
