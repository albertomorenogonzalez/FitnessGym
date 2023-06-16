package com.example.fitnessgym.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.fitnessgym.functions.ChangeLanguage
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityConfigBinding
import java.util.*

class ConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigBinding
    private lateinit var adapterLanguage: ArrayAdapter<String>
    private lateinit var adapterThemes: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize shared preferences
        val sp: SharedPreferences = getSharedPreferences("com.pmdm.juego_CONFIGURACION", MODE_PRIVATE)

        // Inflate the layout using ViewBinding
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set action bar color
        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        // Set action bar title
        supportActionBar?.setTitle(R.string.settings)

        with(binding) {
            // Get the language array and language code array from resources
            val languageArray = resources.getStringArray(R.array.languages)
            val languageCodeArray = resources.getStringArray(R.array.languages_code)

            // Initialize ArrayAdapter with languageArray
            adapterLanguage = ArrayAdapter(root.context, android.R.layout.simple_dropdown_item_1line, languageArray)

            // Set the adapter for the language selector
            selectorLanguage.adapter = adapterLanguage

            // Set the selected language based on the stored value in shared preferences
            selectorLanguage.setSelection(sp.getInt("language_value", 0))

            // Set the placeholder text for the theme selector
            selectorThemeBackgroundText.setText(R.string.soon)

            // Save button click listener
            saveButton.setOnClickListener {
                // Get the selected language value
                val languageValue = selectorLanguage.selectedItemId.toInt()

                // Update shared preferences with the selected language and other configuration values
                sp.edit {
                    putInt("language_value", languageValue)
                    putInt("from_config", 1)
                    putString("language", languageCodeArray[languageValue])
                    commit()
                }

                // Change the app language using the ChangeLanguage function
                ChangeLanguage.changeLanguage(languageCodeArray[languageValue], this@ConfigActivity)

                // Start the MainActivity and clear the back stack
                val intent = Intent(root.context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                // Finish the current activity
                finish()
            }

            // Back button click listener
            backButton.setOnClickListener {
                // Finish the current activity and return to the previous activity
                finish()
                return@setOnClickListener
            }
        }
    }
}
