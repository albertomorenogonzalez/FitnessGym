package com.example.fitnessgym.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.fitnessgym.fragments.*
import com.example.fitnessgym.functions.ChangeLanguage
import com.example.fitnessgym.functions.Dialogs
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.*
import kotlin.properties.Delegates.observable

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp: SharedPreferences = getSharedPreferences("com.pmdm.juego_CONFIGURACION", MODE_PRIVATE)

        sp.getString("language", "es")?.let { ChangeLanguage.changeLanguage(it, this@MainActivity) }

        auth = FirebaseAuth.getInstance()

        Objects.requireNonNull(supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red))))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (sp.getInt("from_config", 0) == 1) {
            val languageArray = resources.getStringArray(R.array.languages)
            val language = languageArray[sp.getInt("language_value", 0)]
            val message = getString(R.string.actual_language, language?.lowercase())
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

            sp.edit {
                putInt("from_config", 0)
                commit()
            }

        } else if (intent.extras?.get("logged") == "logged") {
            val mp = MediaPlayer.create(this, R.raw.login_audio)
            mp.start()

            mp.setOnCompletionListener {
                it.release()
            }
        }

        drawerLayout = findViewById(R.id.side_menu)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment()).commit()
            navView.setCheckedItem(R.id.nav_home)
            supportActionBar?.setTitle(R.string.home)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val headerView = navView.getHeaderView(0)
        val btnLogOut: Chip = headerView.findViewById(R.id.log_out_btn)
        val profilePick: ImageView = headerView.findViewById(R.id.profile_pick)

        btnLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val currentUser = auth.currentUser
        val username : TextView = headerView.findViewById(R.id.user_name)

        if (currentUser != null) {
            val userDocRef = db.collection("usuarios").document(currentUser.uid)
            var userName: String by observable("") { _, _, newUserName ->
                username.text = newUserName
            }
            var photoUrl: String by observable("") { _, _, newPhotoUrl ->
                val firebasePhotoStart = "https://firebasestorage.googleapis.com/v0/b/fitness-gym-80s.appspot.com/o/fitnessgym-images"
                if (firebasePhotoStart in newPhotoUrl) {
                    Glide.with(binding.root).load(newPhotoUrl).into(profilePick)
                } else {
                    profilePick.setImageResource(R.drawable.fitness_gym_logo)
                }

            }

            userDocRef.addSnapshotListener { snapshot: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val newUserName = snapshot.getString("first_name") ?: ""
                    val newPhotoUrl = snapshot.getString("photo") ?: ""

                    userName = newUserName
                    photoUrl = newPhotoUrl
                }
            }

        }

        profilePick.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ProfileFragment())
                .addToBackStack(null)
                .commit()

            drawerLayout.closeDrawer(GravityCompat.START)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.container)) {
                is HomeFragment -> supportActionBar?.setTitle(R.string.home)
                is ProfileFragment -> supportActionBar?.setTitle(R.string.profile)
                is GroupsFragment -> supportActionBar?.setTitle(R.string.groups)
                is CustomersFragment -> supportActionBar?.setTitle(R.string.customers)
                is InstructorsFragment -> supportActionBar?.setTitle(R.string.instructors)
                is AboutFragment -> supportActionBar?.setTitle(R.string.about)
            }
        }

        onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                } else {
                    Dialogs.showExitDialog(this@MainActivity)
                }
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ProfileFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_groups -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, GroupsFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_customers -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CustomersFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_instructors -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, InstructorsFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_about -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AboutFragment())
                    .addToBackStack(null)
                    .commit()
            }
            else -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
}
