package com.example.christ_international

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class FeedbackActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var facultySpinner: MaterialAutoCompleteTextView
    private lateinit var ratingBar: RatingBar
    private lateinit var commentInput: TextInputEditText
    private lateinit var submitButton: MaterialButton
    private var studentRegNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Get student registration number from intent
        studentRegNumber = intent.getStringExtra("student_reg_number")
        if (studentRegNumber == null) {
            Toast.makeText(this, "Error: Student registration number not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        supportActionBar?.title = "Submit Feedback"

        // Initialize views
        facultySpinner = findViewById(R.id.facultySpinner)
        ratingBar = findViewById(R.id.ratingBar)
        commentInput = findViewById(R.id.commentInput)
        submitButton = findViewById(R.id.submitButton)

        // Setup faculty spinner
        setupFacultySpinner()

        // Setup submit button
        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun setupFacultySpinner() {
        try {
            val facultyList = dbHelper.getAllFaculty()
            val facultyNames = facultyList.map { "${it.name} (${it.empId})" }
            val facultyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, facultyNames)
            facultySpinner.setAdapter(facultyAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading faculty list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitFeedback() {
        try {
            val selectedFaculty = facultySpinner.text.toString()
            val rating = ratingBar.rating.toInt()
            val comment = commentInput.text.toString().trim()

            // Validate inputs
            if (selectedFaculty.isEmpty()) {
                Toast.makeText(this, "Please select a faculty member", Toast.LENGTH_SHORT).show()
                return
            }

            if (comment.isEmpty()) {
                Toast.makeText(this, "Please provide feedback comments", Toast.LENGTH_SHORT).show()
                return
            }

            // Extract faculty ID from selection (format: "Name (EmpId)")
            val facultyEmpId = selectedFaculty.substringAfterLast("(").substringBefore(")")
            
            // Get student registration number
            val studentRegNumber = this.studentRegNumber ?: return

            // Submit feedback
            val result = dbHelper.addFeedback(
                studentRegNumber = studentRegNumber,
                facultyEmpId = facultyEmpId,
                rating = rating,
                comment = comment
            )

            if (result > 0) {
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error submitting feedback: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
} 