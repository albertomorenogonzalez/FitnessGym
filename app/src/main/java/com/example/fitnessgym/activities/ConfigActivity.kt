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

        val sp: SharedPreferences = getSharedPreferences("com.pmdm.juego_CONFIGURACION", MODE_PRIVATE)

        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        supportActionBar?.setTitle(R.string.settings)

        with (binding) {

            val languageArray = resources.getStringArray(R.array.languages)
            val languageCodeArray = resources.getStringArray(R.array.languages_code)

            adapterLanguage = ArrayAdapter(root.context, android.R.layout.simple_dropdown_item_1line, languageArray)

            selectorLanguage.adapter = adapterLanguage

            selectorLanguage.setSelection(sp.getInt("language_value", 0))

            selectorThemeBackgroundText.setText(R.string.soon)

            saveButton.setOnClickListener {
                val languageValue  = selectorLanguage.selectedItemId.toInt()

                sp.edit {
                    putInt("language_value", languageValue)
                    putInt("from_config", 1)
                    putString("language", languageCodeArray[languageValue])
                    commit()
                }

                ChangeLanguage.changeLanguage(languageCodeArray[languageValue], this@ConfigActivity)

                val intent = Intent(root.context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            backButton.setOnClickListener {
                finish()
                return@setOnClickListener
            }
        }
    }
}
