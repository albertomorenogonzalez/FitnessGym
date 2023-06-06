package com.example.fitnessgym.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding
import com.google.firebase.firestore.FirebaseFirestore

class InstructorsFragment : Fragment() {

    private lateinit var binding: FragmentInstructorsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()


    }

    companion object {
        @JvmStatic
        fun newInstance() = InstructorsFragment()

    }

}