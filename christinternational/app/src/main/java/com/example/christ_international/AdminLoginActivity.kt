package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AdminLoginActivity : AppCompatActivity() {
    private lateinit var etAdminUsername: TextInputEditText
    private lateinit var etAdminPassword: TextInputEditText
    private lateinit var btnAdminLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        // Initialize views
        etAdminUsername = findViewById(R.id.etAdminUsername)
        etAdminPassword = findViewById(R.id.etAdminPassword)
        btnAdminLogin = findViewById(R.id.btnAdminLogin)

        // Set click listener for login button
        btnAdminLogin.setOnClickListener {
            handleAdminLogin()
        }
    }

    private fun handleAdminLogin() {
        val username = etAdminUsername.text.toString().trim()
        val password = etAdminPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // For demonstration, using hardcoded admin credentials
        // In a real app, these should be securely stored and verified
        if (username == "admin" && password == "admin123") {
            // Navigate to AdminDashboardActivity
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        // Navigate back to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
} 