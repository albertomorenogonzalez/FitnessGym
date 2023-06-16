package com.example.fitnessgym.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    /**
     * @param savedInstanceState: Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp: SharedPreferences = getSharedPreferences("com.pmdm.juego_CONFIGURACION", MODE_PRIVATE)

        sp.getString("language", "es")?.let { ChangeLanguage.changeLanguage(it, this@LoginActivity) }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            loginBtn.setOnClickListener {
                login()
            }

            registerBtn.setOnClickListener {
                val i = Intent(this@LoginActivity, RegisterActivity::class.java)
                answer.launch(i)
            }
        }

        onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTaskRoot) {
                    Dialogs.showExitDialog(this@LoginActivity)
                }
            }
        })
    }

    /**
     * @return
     */
    private fun login() {
        with(binding) {
            val email = txtEmail.text.toString().trim()
            val pw = txtPw.text.toString().trim()

            if (email.isNotEmpty() || email.isNotBlank() && pw.isNotEmpty() || pw.isNotBlank()) {
                Firebase.auth
                    .signInWithEmailAndPassword(email, pw)
                    .addOnSuccessListener {

                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        i.putExtra("logged", "logged")
                        startActivity(i)
                        finish()

                    }
                    .addOnFailureListener {
                        Snackbar.make(root, R.string.login_exception, Snackbar.LENGTH_LONG).show()
                    }

            } else {
                Snackbar.make(root, R.string.fields_exception, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * @param data: ActivityResult
     */
    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            RESULT_OK       -> {
                val email = data.data?.getStringExtra("email")
                val pw = data.data?.getStringExtra("pw")

                with (binding) {
                    txtEmail.setText(email)
                    txtPw.setText(pw)
                }

                Firebase.auth.signOut()

            }
            RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, R.string.cancel, Snackbar.LENGTH_LONG).show()
        }
    }
}