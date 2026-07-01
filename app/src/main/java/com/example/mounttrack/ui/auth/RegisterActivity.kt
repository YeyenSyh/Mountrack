package com.example.mounttrack.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.databinding.ActivityRegisterBinding
import com.example.mounttrack.ui.MainActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua bidang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan akun dummy
            preferenceManager.userName = name
            preferenceManager.userEmail = email
            preferenceManager.userPassword = password
            
            // Langsung login setelah daftar
            preferenceManager.isLoggedIn = true

            Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()

            val mainIntent = Intent(this, MainActivity::class.java).apply {
                putExtras(intent) // Forward extras
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(mainIntent)
            finish()
        }

        binding.tvLoginLink.setOnClickListener {
            finish() // Kembali ke halaman login
        }
    }
}
