package com.example.fitnessgym.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fitnessgym.fragments.CustomersFragment
import com.example.fitnessgym.fragments.GroupsFragment
import com.example.fitnessgym.fragments.HomeFragment
import com.example.fitnessgym.fragments.InstructorsFragment
import com.fitness.fitnessgym.R
import com.fitness.fitnessgym.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var auth: FirebaseAuth

    private val answer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { verify(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            finish()
            return@setOnClickListener
        }

        profilePick.setOnClickListener {
            val intent =  Intent(this@MainActivity, ProfileActivity::class.java)
            answer.launch(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment()).commit()

                supportActionBar?.setTitle(R.string.home)
            }
            R.id.nav_groups -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, GroupsFragment()).commit()

                supportActionBar?.setTitle(R.string.groups)
            }
            R.id.nav_customers -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CustomersFragment()).commit()

                supportActionBar?.setTitle(R.string.customers)
            }
            R.id.nav_instructors -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, InstructorsFragment()).commit()

                supportActionBar?.setTitle(R.string.instructors)
            }
            else -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun verify(data: ActivityResult) {
        when (data.resultCode) {
            RESULT_OK       -> Snackbar.make(binding.root, "ok", Snackbar.LENGTH_LONG).show()
            RESULT_CANCELED -> {}
            else            -> Snackbar.make(binding.root, "canceled", Snackbar.LENGTH_LONG).show()
        }
    }
}

