package com.example.fitnessgym.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val listener: (day:Int, month:Int, year:Int) -> Unit): DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    // Function called when a date is set in the DatePickerDialog
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    // Function to create the DatePickerDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Get current date
        val c = Calendar.getInstance()

        // Extract day, month, and year from the current date
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        // Create a new DatePickerDialog with the current date as the initial selection
        val picker = DatePickerDialog(activity as Context, this, year, month, day)

        // Set the maximum date to restrict selecting future dates
        picker.datePicker.maxDate = c.timeInMillis

        // Return the created DatePickerDialog
        return picker
    }
}
