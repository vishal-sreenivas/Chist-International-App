package com.example.christ_international

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class EventAdapter(
    private val events: List<Event>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: MaterialCardView = view.findViewById(R.id.eventCard)
        val eventImage: ImageView = view.findViewById(R.id.eventImage)
        val eventTitle: TextView = view.findViewById(R.id.eventTitle)
        val eventDate: TextView = view.findViewById(R.id.eventDate)
        val eventTime: TextView = view.findViewById(R.id.eventTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        
        holder.eventTitle.text = event.title
        holder.eventDate.text = event.date
        holder.eventTime.text = event.time
        
        // Set different images based on event title/type
        val imageResource = when {
            event.title.contains("Gateways", ignoreCase = true) -> R.drawable.event1
            event.title.contains("Cultural", ignoreCase = true) -> R.drawable.event2
            event.title.contains("Career", ignoreCase = true) -> R.drawable.event3
            else -> R.drawable.event5 // Default image
        }
        
        holder.eventImage.setImageResource(imageResource)
        
        holder.cardView.setOnClickListener {
            onEventClick(event)
        }
    }

    override fun getItemCount() = events.size
} 