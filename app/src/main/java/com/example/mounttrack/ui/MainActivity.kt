package com.example.mounttrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mounttrack.R
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.databinding.ActivityMainBinding

/**
 * Activity utama yang menampung Bottom Navigation Bar dan NavHostFragment.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Terapkan preferensi tema sebelum super.onCreate
        preferenceManager = PreferenceManager(this)
        applyAppTheme(preferenceManager.isDarkMode)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cari NavHostFragment dari layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Hubungkan Bottom Navigation dengan NavController
        binding.bottomNav.setupWithNavController(navController)
    }

    /**
     * Menerapkan tema gelap atau terang secara dinamis.
     */
    private fun applyAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
