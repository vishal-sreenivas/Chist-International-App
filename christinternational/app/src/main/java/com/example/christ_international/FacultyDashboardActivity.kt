package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView

class FacultyDashboardActivity : AppCompatActivity() {
    private var tvWelcome: TextView? = null
    private var tvNotificationCount: TextView? = null
    private lateinit var dbHelper: DatabaseHelper
    private var facultyEmpId: String? = null
    private var facultyName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_dashboard)

        try {
            // Initialize database helper
            dbHelper = DatabaseHelper(this)

            // Setup toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = getString(R.string.faculty_dashboard)
            }

            // Get faculty details from intent
            facultyName = intent.getStringExtra("faculty_name")
            facultyEmpId = intent.getStringExtra("faculty_emp_id")

            if (facultyEmpId == null) {
                Toast.makeText(this, "Error: Faculty ID not found", Toast.LENGTH_SHORT).show()
                logout()
                return
            }

            // Initialize views
            initializeViews()

            // Set welcome message
            tvWelcome?.text = getString(R.string.welcome_message, facultyName ?: "Professor")

            // Set notification count
            updateNotificationCount()

            // Setup click listeners for each module
            setupModuleClickListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_initializing_dashboard, e.message), Toast.LENGTH_LONG).show()
        }
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

    private fun initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvNotificationCount = findViewById(R.id.tvNotificationCount)
    }

    private fun updateNotificationCount() {
        try {
            // Get count of pending appointments
            val pendingCount = facultyEmpId?.let { empId ->
                dbHelper.getFacultyAppointmentsByStatus(empId, "PENDING").size
            } ?: 0

            tvNotificationCount?.text = if (pendingCount > 0) {
                "You have $pendingCount pending appointment(s)"
            } else {
                "No pending appointments"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvNotificationCount?.text = "No pending appointments"
        }
    }

    private fun setupModuleClickListeners() {
        // International Students
        findViewById<MaterialCardView>(R.id.cardInternationalStudents).setOnClickListener {
            startActivity(Intent(this, InternationalStudentsActivity::class.java))
        }

        // Events
        findViewById<MaterialCardView>(R.id.cardEvents).setOnClickListener {
            startActivity(Intent(this, EventUpdatesActivity::class.java))
        }

        // Appointments
        findViewById<MaterialCardView>(R.id.cardAppointments).setOnClickListener {
            val intent = Intent(this, FacultyAppointmentsActivity::class.java)
            intent.putExtra("faculty_emp_id", facultyEmpId)
            startActivity(intent)
        }

        // Feedback
        findViewById<MaterialCardView>(R.id.cardFeedback)?.setOnClickListener {
            showFeedbackDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update notification count when returning to the dashboard
        updateNotificationCount()
    }

    private fun showFeedbackDialog() {
        val facultyEmpId = this.facultyEmpId ?: return
        val feedbackList = dbHelper.getFacultyFeedback(facultyEmpId)

        if (feedbackList.isEmpty()) {
            Toast.makeText(this, "No feedback available", Toast.LENGTH_SHORT).show()
            return
        }

        val message = buildString {
            feedbackList.forEach { feedback ->
                append("Student: ${feedback.studentRegNumber}\n")
                append("Rating: ${feedback.rating}/5\n")
                append("Comment: ${feedback.comment}\n\n")
            }
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Student Feedback")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onBackPressed() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            dbHelper.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}