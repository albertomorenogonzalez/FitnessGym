package com.example.fitnessgym.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.fitnessgym.activities.ConfigActivity
import com.example.fitnessgym.activities.MainActivity
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding
import com.fitness.fitnessgym.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val configButton: ImageView? = view.findViewById(R.id.config_button)

        configButton?.setOnClickListener {
            val intent = Intent(activity, ConfigActivity::class.java)
            startActivity(intent)
        }

        return view
    }



    companion object {
        @JvmStatic
        fun newInstance() {

        }

    }
}