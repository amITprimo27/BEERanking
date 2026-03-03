package com.example.beeranking.base

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.beeranking.R
import com.example.beeranking.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.mainNavHost) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.editPostFragment, R.id.addPostFragment -> {
                    bottomNav.visibility = View.GONE
                }
                else -> {
                    val isBeerDestination = destination.parent?.id == R.id.app_graph
                    bottomNav.visibility = if (isBeerDestination) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
