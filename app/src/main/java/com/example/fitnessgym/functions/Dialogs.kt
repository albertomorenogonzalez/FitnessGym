package com.example.fitnessgym.functions

import android.app.Activity
import com.fitness.fitnessgym.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Dialogs {

    companion object {
        // Method to show an exit dialog to confirm closing the application
        fun showExitDialog(activity: Activity) {
            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.close_application) // Set the dialog title
                .setMessage(R.string.close_application_message) // Set the dialog message
                .setPositiveButton(R.string.yes) { _, _ ->
                    // If "Yes" is clicked, finish the activity and its parent activities
                    activity.finishAffinity()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    // If "No" is clicked, dismiss the dialog
                    dialog.dismiss()
                }
                .show() // Show the dialog
        }
    }
}
