package com.example.christ_international

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class UpdateEventsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etEventTitle: TextInputEditText
    private lateinit var etEventDate: TextInputEditText
    private lateinit var etEventTime: TextInputEditText
    private lateinit var etEventDescription: TextInputEditText
    private lateinit var etRegistrationLink: TextInputEditText
    private lateinit var ivEventImage: ImageView
    private lateinit var btnSelectImage: MaterialButton
    private lateinit var btnSaveEvent: MaterialButton
    
    private var selectedImageUri: Uri? = null
    private var eventToEdit: Event? = null
    private val calendar = Calendar.getInstance()

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            ivEventImage.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_events)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Initialize views
        initializeViews()
        setupListeners()

        // Check if we're editing an existing event
        val eventId = intent.getIntExtra("event_id", -1)
        if (eventId != -1) {
            loadEvent(eventId)
        }
    }

    private fun initializeViews() {
        // Initialize EditTexts
        etEventTitle = findViewById(R.id.etEventTitle)
        etEventDate = findViewById(R.id.etEventDate)
        etEventTime = findViewById(R.id.etEventTime)
        etEventDescription = findViewById(R.id.etEventDescription)
        etRegistrationLink = findViewById(R.id.etRegistrationLink)
        
        // Initialize ImageView and Buttons
        ivEventImage = findViewById(R.id.ivEventImage)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSaveEvent = findViewById(R.id.btnSaveEvent)

        // Set up toolbar
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (eventToEdit != null) "Edit Event" else "Add New Event"
    }

    private fun setupListeners() {
        // Date picker
        etEventDate.setOnClickListener {
            showDatePicker()
        }

        // Time picker
        etEventTime.setOnClickListener {
            showTimePicker()
        }

        // Image selection
        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        // Save event
        btnSaveEvent.setOnClickListener {
            saveEvent()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeField()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateDateField() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etEventDate.setText(dateFormat.format(calendar.time))
    }

    private fun updateTimeField() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        etEventTime.setText(timeFormat.format(calendar.time))
    }

    private fun loadEvent(eventId: Int) {
        eventToEdit = dbHelper.getEvent(eventId)
        eventToEdit?.let { event ->
            etEventTitle.setText(event.title)
            etEventDate.setText(event.date)
            etEventTime.setText(event.time)
            etEventDescription.setText(event.description)
            etRegistrationLink.setText(event.registrationLink)
            
            // Load image if available
            if (event.imageUrl.isNotEmpty()) {
                selectedImageUri = Uri.parse(event.imageUrl)
                ivEventImage.setImageURI(selectedImageUri)
            }
        }
    }

    private fun saveEvent() {
        val title = etEventTitle.text.toString().trim()
        val date = etEventDate.text.toString().trim()
        val time = etEventTime.text.toString().trim()
        val description = etEventDescription.text.toString().trim()
        val registrationLink = etRegistrationLink.text.toString().trim()

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(
            id = eventToEdit?.id ?: 0,
            title = title,
            description = description,
            date = date,
            time = time,
            imageUrl = selectedImageUri?.toString() ?: "",
            registrationLink = registrationLink
        )

        val success = if (eventToEdit != null) {
            dbHelper.updateEvent(event)
        } else {
            dbHelper.addEvent(event) > 0
        }

        if (success) {
            Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error saving event", Toast.LENGTH_SHORT).show()
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