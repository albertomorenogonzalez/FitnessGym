package com.example.fitnessgym.functions

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.fitnessgym.fragments.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class Dates {

    companion object {
        // Define two SimpleDateFormat objects for formatting dates
        val sourceFormatter : SimpleDateFormat = SimpleDateFormat(
            "dd/MM/yyyy", Locale.GERMANY
        )
        val targetFormatter : SimpleDateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mmXXX", Locale.GERMANY
        )

        // Show a date picker dialog
        fun showDatePickerDialog(spm: FragmentManager, view: TextView) {
            val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year, view) }
            datePicker.show(spm, "datePicker")
        }

        @SuppressLint("SetTextI18n")
        // Callback function for date selection
        fun onDateSelected(day: Int, month:Int, year:Int, view: TextView) {
            // Update the text view with the selected date in the desired format
            view.text = String.format("%02d/%02d/%04d", day, month + 1, year)
        }

        // Format a date string from one format to another
        fun formatDate(dateString: String) : String {
            val date: Date = sourceFormatter.parse(dateString) as Date
            return targetFormatter.format(date)
        }

        // Format a date string to a proper display format
        fun showProperDate(dateString: String) : String {
            val date: Date = targetFormatter.parse(dateString) as Date
            return sourceFormatter.format(date)
        }
    }
}
