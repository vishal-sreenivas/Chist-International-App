package com.example.christ_international

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class EventUpdatesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var fragmentContainer: View
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_updates)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Set up toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Event Updates"

        // Initialize views
        recyclerView = findViewById(R.id.eventsRecyclerView)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Load events from database
        loadEvents()
    }

    private fun loadEvents() {
        val events = dbHelper.getAllEvents()
        
        // Initialize and set adapter
        eventAdapter = EventAdapter(events) { event ->
            showEventDetails(event)
        }
        recyclerView.adapter = eventAdapter
    }

    override fun onResume() {
        super.onResume()
        // Reload events when returning to this activity
        loadEvents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // If fragment is visible, hide it and show RecyclerView
            if (fragmentContainer.visibility == View.VISIBLE) {
                supportFragmentManager.popBackStack()
                fragmentContainer.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                return true
            }
            // Otherwise, handle normal back button press
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showEventDetails(event: Event) {
        // Show fragment container and hide RecyclerView
        fragmentContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // Create and show fragment
        val fragment = EventDetailFragment.newInstance(event)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        // If fragment is visible, hide it and show RecyclerView
        if (fragmentContainer.visibility == View.VISIBLE) {
            supportFragmentManager.popBackStack()
            fragmentContainer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            return
        }
        // Otherwise, handle normal back button press
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
} 