package com.udacity.project.spire

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.udacity.project.spire.databinding.ActivityMainBinding

/**
 * Main activity that hosts navigation and manages the app's UI.
 *
 * TODO #46: Implement Navigation Component setup
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    /**
     * Top-level destinations (show bottom nav, no up button).
     * These are the main tabs: Buildings, Countries, My Visits, Statistics.
     */
    private val topLevelDestinations = setOf(
        R.id.buildingsFragment,
        R.id.myVisitsFragment,
        R.id.statisticsFragment
    )

    /**
     * TODO #46a: Implement navController property
     */
    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /**
         * TODO #46b: Configure AppBarConfiguration and Navigation
         */
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        setupBottomNavVisibility()
    }

    /**
     * TODO #46c: Implement setupBottomNavVisibility()
     */
    private fun setupBottomNavVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    /**
     * TODO #46d: Implement onSupportNavigateUp()
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
