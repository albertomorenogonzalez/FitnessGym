package com.example.fitnessgym.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding

class InstructorsFragment : Fragment() {

    private lateinit var binding: FragmentInstructorsBinding

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

}