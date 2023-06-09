package com.example.fitnessgym.functions

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.fitnessgym.fragments.DatePickerFragment

class Dates {

    companion object {
         fun showDatePickerDialog(spm: FragmentManager, view: TextView) {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year, view) }

            datePicker.show(spm, "datePicker")
        }

        @SuppressLint("SetTextI18n")
        fun onDateSelected(day: Int, month:Int, year:Int, view: TextView) {
            view.text = String.format("%02d/%02d/%04d", day, month + 1, year)
        }
    }

}