package com.example.fitnessgym.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessgym.activities.AddEditCustomerActivity
import com.example.fitnessgym.activities.AddEditGroupActivity
import com.example.fitnessgym.activities.ConfigActivity
import com.example.fitnessgym.activities.MainActivity
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.root.setOnLongClickListener {
            val popupMenu = PopupMenu(context, binding.welcomeText)
            popupMenu.menuInflater.inflate(R.menu.context_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                val form = "add"

                when (menuItem.itemId) {
                    R.id.go_to_settings -> {
                        val i = Intent(context, ConfigActivity::class.java)
                        answer.launch(i)
                        true
                    }
                    R.id.add_customer -> {
                        val i = Intent(context, AddEditCustomerActivity::class.java)
                        i.putExtra("form", form)
                        answer.launch(i)
                        true
                    }
                    R.id.add_group -> {
                        val i = Intent(context, AddEditGroupActivity::class.java)
                        i.putExtra("form", form)
                        answer.launch(i)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()

            true
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        with (binding) {
            customerButton.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, CustomersFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }

            groupsButton.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, GroupsFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    /**
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            AppCompatActivity.RESULT_OK -> {

                val name = data.data?.getStringExtra("name")

                if (name == "group") {
                    Snackbar.make(binding.root, R.string.group_added_successfully, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, R.string.customer_successfully_added, Snackbar.LENGTH_LONG).show()
                }

            }
            AppCompatActivity.RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}