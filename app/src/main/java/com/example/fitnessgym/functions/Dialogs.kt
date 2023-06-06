package com.example.fitnessgym.functions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.fitness.fitnessgym.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Dialogs {

    companion object {
        fun showExitDialog(activity: Activity) {
            MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.close_application)
                .setMessage(R.string.close_application_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    activity.finishAffinity()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

}