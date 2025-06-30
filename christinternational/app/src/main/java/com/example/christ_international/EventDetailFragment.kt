package com.example.christ_international

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class EventDetailFragment : Fragment() {
    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = Event(
                it.getInt(ARG_EVENT_ID),
                it.getString(ARG_EVENT_TITLE) ?: "",
                it.getString(ARG_EVENT_DESCRIPTION) ?: "",
                it.getString(ARG_EVENT_DATE) ?: "",
                it.getString(ARG_EVENT_TIME) ?: "",
                it.getString(ARG_EVENT_IMAGE_URL) ?: "",
                it.getString(ARG_EVENT_REGISTRATION_LINK) ?: ""
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        event?.let { event ->
            view.findViewById<TextView>(R.id.eventDetailTitle).text = event.title
            view.findViewById<TextView>(R.id.eventDetailDescription).text = event.description
            view.findViewById<TextView>(R.id.eventDetailDateTime).text = 
                "${event.date} at ${event.time}"

            // Set event image
            val imageResource = when {
                event.title.contains("Gateways", ignoreCase = true) -> R.drawable.event1
                event.title.contains("Cultural", ignoreCase = true) -> R.drawable.event2
                event.title.contains("Career", ignoreCase = true) -> R.drawable.event3
                else -> R.drawable.event5
            }
            view.findViewById<ImageView>(R.id.eventDetailImage).setImageResource(imageResource)

            // Set up register button
            view.findViewById<Button>(R.id.registerButton).setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.registrationLink))
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val ARG_EVENT_ID = "event_id"
        private const val ARG_EVENT_TITLE = "event_title"
        private const val ARG_EVENT_DESCRIPTION = "event_description"
        private const val ARG_EVENT_DATE = "event_date"
        private const val ARG_EVENT_TIME = "event_time"
        private const val ARG_EVENT_IMAGE_URL = "event_image_url"
        private const val ARG_EVENT_REGISTRATION_LINK = "event_registration_link"

        @JvmStatic
        fun newInstance(event: Event) =
            EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_EVENT_ID, event.id)
                    putString(ARG_EVENT_TITLE, event.title)
                    putString(ARG_EVENT_DESCRIPTION, event.description)
                    putString(ARG_EVENT_DATE, event.date)
                    putString(ARG_EVENT_TIME, event.time)
                    putString(ARG_EVENT_IMAGE_URL, event.imageUrl)
                    putString(ARG_EVENT_REGISTRATION_LINK, event.registrationLink)
                }
            }
    }
} 