package com.example.fitnessgym.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentCustomersBinding
import com.fitness.fitnessgym.databinding.FragmentInstructorsBinding

class CustomersFragment : Fragment() {

    private lateinit var binding: FragmentCustomersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CustomersFragment()

    }
}