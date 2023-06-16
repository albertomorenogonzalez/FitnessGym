package com.example.fitnessgym.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnessgym.functions.ChangeLanguage
import com.example.fitnessgym.functions.Dialogs
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    /**
     * @param savedInstanceState: Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp: SharedPreferences = getSharedPreferences("com.pmdm.juego_CONFIGURACION", MODE_PRIVATE)

        // Retrieve the language preference from SharedPreferences and change the language
        sp.getString("language", "es")?.let { ChangeLanguage.changeLanguage(it, this@LoginActivity) }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // If a user is already logged in, redirect to the main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            // Handle login button click
            loginBtn.setOnClickListener {
                login()
            }

            // Handle register button click
            registerBtn.setOnClickListener {
                val i = Intent(this@LoginActivity, RegisterActivity::class.java)
                answer.launch(i)
            }
        }

        // Handle the back button press
        onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTaskRoot) {
                    // Show exit dialog if the login activity is the root of the task
                    Dialogs.showExitDialog(this@LoginActivity)
                }
            }
        })
    }

    /**
     * Perform login with the provided email and password
     */
    private fun login() {
        with(binding) {
            val email = txtEmail.text.toString().trim()
            val pw = txtPw.text.toString().trim()

            if (email.isNotEmpty() || email.isNotBlank() && pw.isNotEmpty() || pw.isNotBlank()) {
                // Sign in with email and password using Firebase Authentication
                Firebase.auth
                    .signInWithEmailAndPassword(email, pw)
                    .addOnSuccessListener {
                        // If login is successful, redirect to the main activity
                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        i.putExtra("logged", "logged")
                        startActivity(i)
                        finish()
                    }
                    .addOnFailureListener {
                        // Show error message if login fails
                        Snackbar.make(root, R.string.login_exception, Snackbar.LENGTH_LONG).show()
                    }
            } else {
                // Show error message if email or password is empty
                Snackbar.make(root, R.string.fields_exception, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Handle the result of the activity launched with startActivityForResult
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            RESULT_OK       -> {
                // Retrieve email and password from the registration activity's result
                val email = data.data?.getStringExtra("email")
                val pw = data.data?.getStringExtra("pw")

                with (binding) {
                    // Set the retrieved email and password in the corresponding text fields
                    txtEmail.setText(email)
                    txtPw.setText(pw)
                }

                // Sign out the user after registration
                Firebase.auth.signOut()
            }
            RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }
}
