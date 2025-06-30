package com.example.christ_international

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.widget.LinearLayout
import android.view.LayoutInflater

class AppointmentBookingActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var facultySpinner: AutoCompleteTextView
    private lateinit var timeSlotSpinner: AutoCompleteTextView
    private lateinit var reasonInput: TextInputEditText
    private lateinit var bookButton: MaterialButton
    private lateinit var appointmentsContainer: LinearLayout
    private lateinit var noAppointmentsText: TextView
    private var studentRegNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_booking)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Get student registration number from intent
        studentRegNo = intent.getStringExtra("REG_NO")
        if (studentRegNo == null) {
            Toast.makeText(this, "Error: Student registration number not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Book Appointment"

        // Initialize views
        initializeViews()
        setupSpinners()
        setupBookButton()
        refreshAppointments()
    }

    private fun initializeViews() {
        facultySpinner = findViewById(R.id.facultySpinner)
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner)
        reasonInput = findViewById(R.id.reasonInput)
        bookButton = findViewById(R.id.bookButton)
        appointmentsContainer = findViewById(R.id.appointmentsContainer)
        noAppointmentsText = findViewById(R.id.noAppointmentsText)
    }

    private fun setupSpinners() {
        // Set up faculty spinner
        val faculty = dbHelper.getAllFaculty()
        val facultyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            faculty.map { "${it.name} (${it.empId})" }
        )
        facultySpinner.setAdapter(facultyAdapter)

        // Set up time slot spinner
        val timeSlots = arrayOf(
            "9:00 AM - 10:00 AM",
            "10:00 AM - 11:00 AM",
            "11:00 AM - 12:00 PM",
            "2:00 PM - 3:00 PM",
            "3:00 PM - 4:00 PM"
        )
        val timeSlotAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            timeSlots
        )
        timeSlotSpinner.setAdapter(timeSlotAdapter)
    }

    private fun setupBookButton() {
        bookButton.setOnClickListener {
            val selectedFaculty = facultySpinner.text.toString()
            val timeSlot = timeSlotSpinner.text.toString()
            val reason = reasonInput.text.toString()

            if (selectedFaculty.isEmpty() || timeSlot.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Extract faculty ID from selection (Format: "Name (EmpID)")
            val facultyEmpId = selectedFaculty.substringAfterLast("(").substringBefore(")")

            // Book appointment
            val result = dbHelper.addAppointment(studentRegNo!!, facultyEmpId, timeSlot, reason)
            when (result) {
                -1L -> Toast.makeText(this, "Error booking appointment", Toast.LENGTH_SHORT).show()
                -2L -> Toast.makeText(this, "You already have an appointment with this faculty at this time", Toast.LENGTH_LONG).show()
                -3L -> Toast.makeText(this, "Selected time slot is not available for this faculty", Toast.LENGTH_LONG).show()
                else -> {
                    Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show()
                    // Clear inputs
                    facultySpinner.text.clear()
                    timeSlotSpinner.text.clear()
                    reasonInput.text?.clear()
                    // Refresh appointments list
                    refreshAppointments()
                }
            }
        }
    }

    private fun refreshAppointments() {
        try {
            val appointments = dbHelper.getStudentAppointments(studentRegNo!!)
            
            if (appointments.isEmpty()) {
                noAppointmentsText.visibility = View.VISIBLE
                appointmentsContainer.visibility = View.GONE
            } else {
                noAppointmentsText.visibility = View.GONE
                appointmentsContainer.visibility = View.VISIBLE
                appointmentsContainer.removeAllViews()

                appointments.forEach { appointment ->
                    val appointmentView = LayoutInflater.from(this)
                        .inflate(R.layout.item_appointment_status, appointmentsContainer, false)

                    val facultyName = dbHelper.getAllFaculty()
                        .find { it.empId == appointment.facultyEmpId }?.name ?: "Faculty"

                    // Set status message
                    appointmentView.findViewById<TextView>(R.id.statusMessage).text = when (appointment.status.uppercase()) {
                        "PENDING" -> "Appointment with $facultyName is pending approval"
                        "APPROVED" -> "Appointment with $facultyName has been approved"
                        "REJECTED" -> "Appointment with $facultyName has been rejected"
                        else -> "Appointment status: ${appointment.status}"
                    }

                    // Set appointment details
                    appointmentView.findViewById<TextView>(R.id.appointmentDetails).text = """
                        Time: ${appointment.timeSlot}
                        Reason: ${appointment.reason}
                    """.trimIndent()

                    // Set status chip
                    appointmentView.findViewById<com.google.android.material.chip.Chip>(R.id.statusChip).apply {
                        text = appointment.status.uppercase()
                        setChipBackgroundColorResource(
                            when (appointment.status.uppercase()) {
                                "PENDING" -> android.R.color.holo_orange_dark
                                "APPROVED" -> android.R.color.holo_green_dark
                                "REJECTED" -> android.R.color.holo_red_dark
                                else -> android.R.color.darker_gray
                            }
                        )
                    }

                    appointmentsContainer.addView(appointmentView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading appointments", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
} 