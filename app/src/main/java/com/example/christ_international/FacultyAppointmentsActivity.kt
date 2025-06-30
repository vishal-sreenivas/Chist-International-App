package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class FacultyAppointmentsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var tabLayout: TabLayout
    private var facultyEmpId: String? = null
    private var currentStatus: String = "PENDING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_appointments)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Appointments"

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Get faculty ID from intent
        facultyEmpId = intent.getStringExtra("faculty_emp_id")
        if (facultyEmpId == null) {
            Toast.makeText(this, "Error: Faculty ID not found", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // Initialize views
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        tabLayout = findViewById(R.id.tabLayout)

        // Set up RecyclerView
        setupRecyclerView()

        // Set up tab selection listener
        setupTabLayout()

        // Load initial appointments (Pending)
        loadAppointments("PENDING")
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

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(
            appointments = emptyList(),
            onApprove = { appointment -> updateAppointmentStatus(appointment.id, "APPROVED") },
            onReject = { appointment -> updateAppointmentStatus(appointment.id, "REJECTED") }
        )

        appointmentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FacultyAppointmentsActivity)
            adapter = appointmentAdapter
        }
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentStatus = when (tab?.position) {
                    0 -> "PENDING"
                    1 -> "APPROVED"
                    2 -> "REJECTED"
                    else -> "PENDING"
                }
                loadAppointments(currentStatus)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Refresh the current tab when reselected
                loadAppointments(currentStatus)
            }
        })
    }

    private fun loadAppointments(status: String) {
        try {
            facultyEmpId?.let { empId ->
                val appointments = dbHelper.getFacultyAppointmentsByStatus(empId, status)
                updateUI(appointments)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading appointments: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(appointments: List<Appointment>) {
        if (appointments.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            appointmentsRecyclerView.visibility = View.GONE
            when (currentStatus) {
                "PENDING" -> emptyView.text = "No pending appointments"
                "APPROVED" -> emptyView.text = "No approved appointments"
                "REJECTED" -> emptyView.text = "No rejected appointments"
            }
        } else {
            emptyView.visibility = View.GONE
            appointmentsRecyclerView.visibility = View.VISIBLE
            appointmentAdapter.updateAppointments(appointments)
        }
    }

    private fun updateAppointmentStatus(appointmentId: Int, newStatus: String) {
        try {
            val result = dbHelper.updateAppointmentStatus(appointmentId, newStatus)
            if (result > 0) {
                Toast.makeText(this, "Appointment $newStatus", Toast.LENGTH_SHORT).show()
                
                // If we're in the pending tab and approved/rejected an appointment,
                // we should switch to the appropriate tab
                if (currentStatus == "PENDING") {
                    when (newStatus) {
                        "APPROVED" -> tabLayout.getTabAt(1)?.select()
                        "REJECTED" -> tabLayout.getTabAt(2)?.select()
                    }
                } else {
                    // Otherwise, just refresh the current tab
                    loadAppointments(currentStatus)
                }
            } else {
                Toast.makeText(this, "Error updating appointment status", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh appointments when returning to the activity
        loadAppointments(currentStatus)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
} 