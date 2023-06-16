package com.example.fitnessgym.functions

import android.app.Activity
import java.util.*

class ChangeLanguage {

    companion object {
        // Method to change the language of the activity
        fun changeLanguage(code: String, activity: Activity) {
            // Get the current configuration of the activity's resources
            val configuration = activity.resources.configuration
            // Set the new locale based on the provided language code
            configuration.setLocale(Locale(code))
            // Update the configuration and resources with the new locale
            activity.resources.updateConfiguration(
                configuration, activity.resources.displayMetrics)
        }
    }
}
