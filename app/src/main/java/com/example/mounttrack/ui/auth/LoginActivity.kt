package com.example.mounttrack.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.databinding.ActivityLoginBinding
import com.example.mounttrack.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Dummy login logic using SharedPreferences
            val savedEmail = preferenceManager.userEmail
            val savedPassword = preferenceManager.userPassword

            if (email == savedEmail && password == savedPassword && savedEmail.isNotEmpty()) {
                // Berhasil login
                preferenceManager.isLoggedIn = true
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    putExtras(intent) // Forward extras like EXTRA_LAT, EXTRA_LON
                }
                startActivity(mainIntent)
                finish()
            } else {
                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegisterLink.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java).apply {
                putExtras(intent)
            }
            startActivity(registerIntent)
        }
    }
}
