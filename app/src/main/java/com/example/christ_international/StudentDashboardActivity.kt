package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView

class StudentDashboardActivity : AppCompatActivity() {
    private var studentRegNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get student information from intent
        studentRegNumber = intent.getStringExtra("student_reg_number")
        if (studentRegNumber == null) {
            Toast.makeText(this, "Error: Student registration number not found", Toast.LENGTH_SHORT).show()
            logout()
            return
        }
        
        // Update welcome message
        findViewById<TextView>(R.id.welcomeText).text = "Welcome, Student"
        findViewById<TextView>(R.id.regNumberText).text = "Reg. Number: $studentRegNumber"

        // Set up click listeners for feature cards
        setupCardClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Navigate back to MainActivity (login screen)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCardClickListeners() {
        // Events Updates
        findViewById<MaterialCardView>(R.id.eventsCard).setOnClickListener {
            try {
                val intent = Intent(this, EventUpdatesActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: Unable to open Events. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

        // Language Translator
        findViewById<MaterialCardView>(R.id.translatorCard).setOnClickListener {
            try {
                val intent = Intent(this, TranslatorActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: Unable to open Translator. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

        // Appointment Booking
        findViewById<MaterialCardView>(R.id.appointmentCard).setOnClickListener {
            try {
                if (studentRegNumber != null) {
                    val intent = Intent(this, AppointmentBookingActivity::class.java).apply {
                        putExtra("REG_NO", studentRegNumber)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error: Student registration number not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: Unable to open appointment booking. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

        // Campus Navigation
        findViewById<MaterialCardView>(R.id.navigationCard).setOnClickListener {
            Toast.makeText(this, "Campus Map feature is under development. Coming soon!", Toast.LENGTH_LONG).show()
        }

        // Chatbot
        findViewById<MaterialCardView>(R.id.chatbotCard).setOnClickListener {
            try {
                val intent = Intent(this, ChatbotActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: Unable to open Chatbot. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

        // Feedback & Reporting
        findViewById<MaterialCardView>(R.id.feedbackCard).setOnClickListener {
            try {
                if (studentRegNumber != null) {
                    val intent = Intent(this, FeedbackActivity::class.java).apply {
                        putExtra("student_reg_number", studentRegNumber)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Error: Student registration number not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: Unable to open feedback form. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }
} 