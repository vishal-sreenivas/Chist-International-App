package com.example.christ_international

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.christ_international.data.Student

class StudentAdapter(private var students: List<Student> = listOf()) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    fun updateStudents(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRegisterNumber: TextView = itemView.findViewById(R.id.tvRegisterNumber)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvClass: TextView = itemView.findViewById(R.id.tvClass)
        private val tvProgram: TextView = itemView.findViewById(R.id.tvProgram)
        private val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)
        private val tvCampus: TextView = itemView.findViewById(R.id.tvCampus)
        private val tvNationality: TextView = itemView.findViewById(R.id.tvNationality)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)

        fun bind(student: Student) {
            tvRegisterNumber.text = student.regNumber
            tvName.text = student.name
            tvEmail.text = student.email
            tvClass.text = student.className
            tvProgram.text = student.program
            tvLevel.text = student.level
            tvCampus.text = student.campus
            tvNationality.text = student.nationality
            tvCategory.text = student.category
        }
    }
}
