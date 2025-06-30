package com.example.christ_international

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    private var students = mutableListOf<Student>()

    fun updateStudents(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount() = students.size

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvRegisterNumber = itemView.findViewById<TextView>(R.id.tvRegisterNumber)
        private val tvSchool = itemView.findViewById<TextView>(R.id.tvSchool)
        private val tvDepartment = itemView.findViewById<TextView>(R.id.tvDepartment)
        private val tvNationality = itemView.findViewById<TextView>(R.id.tvNationality)

        fun bind(student: Student) {
            tvName.text = student.name
            tvRegisterNumber.text = student.registerNumber
            tvSchool.text = student.school
            tvDepartment.text = student.department
            tvNationality.text = student.nationality
        }
    }
} 