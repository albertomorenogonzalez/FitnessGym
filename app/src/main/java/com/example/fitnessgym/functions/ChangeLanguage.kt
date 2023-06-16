package com.example.fitnessgym.functions

import android.app.Activity
import java.util.*

class ChangeLanguage {

    companion object {
        fun changeLanguage(code: String, activity: Activity) {
            val configuration = activity.resources.configuration
            configuration.setLocale(Locale(code))
            activity.resources.updateConfiguration(
                configuration, activity.resources.displayMetrics)

        }
    }

}