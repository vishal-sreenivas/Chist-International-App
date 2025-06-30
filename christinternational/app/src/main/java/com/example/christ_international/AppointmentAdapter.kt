package com.example.christ_international

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onApprove: (Appointment) -> Unit,
    private val onReject: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val studentName: TextView = view.findViewById(R.id.studentName)
        val studentRegNo: TextView = view.findViewById(R.id.studentRegNo)
        val timeSlot: TextView = view.findViewById(R.id.timeSlot)
        val reason: TextView = view.findViewById(R.id.reason)
        val actionButtons: LinearLayout = view.findViewById(R.id.actionButtons)
        val approveButton: MaterialButton = view.findViewById(R.id.approveButton)
        val rejectButton: MaterialButton = view.findViewById(R.id.rejectButton)
        val statusText: TextView = view.findViewById(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val context = holder.itemView.context
        val dbHelper = DatabaseHelper(context)

        try {
            // Get student name from database
            val studentName = dbHelper.getStudentName(appointment.studentRegNumber) ?: "Unknown Student"
            
            // Set student details
            holder.studentName.text = studentName
            holder.studentRegNo.text = "Reg No: ${appointment.studentRegNumber}"
            holder.timeSlot.text = "Time: ${appointment.timeSlot}"
            holder.reason.text = "Reason: ${appointment.reason}"

            // Handle status and buttons visibility
            when (appointment.status.uppercase()) {
                "PENDING" -> {
                    holder.actionButtons.visibility = View.VISIBLE
                    holder.statusText.visibility = View.GONE
                    
                    holder.approveButton.setOnClickListener { 
                        onApprove(appointment)
                    }
                    holder.rejectButton.setOnClickListener { 
                        onReject(appointment)
                    }
                }
                "APPROVED" -> {
                    holder.actionButtons.visibility = View.GONE
                    holder.statusText.apply {
                        visibility = View.VISIBLE
                        text = "Status: Approved"
                        setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
                    }
                }
                "REJECTED" -> {
                    holder.actionButtons.visibility = View.GONE
                    holder.statusText.apply {
                        visibility = View.VISIBLE
                        text = "Status: Rejected"
                        setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dbHelper.close()
        }
    }

    override fun getItemCount() = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
} 