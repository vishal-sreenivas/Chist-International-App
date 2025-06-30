package com.example.christ_international

data class Student(
    val regNumber: String,
    val name: String,
    val email: String,
    val department: String? = null,
    val course: String? = null
)

data class Faculty(
    val empId: String,
    val email: String,
    val name: String,
    val department: String? = null
)

data class Feedback(
    val studentRegNumber: String,
    val facultyEmpId: String,
    val rating: Int,
    val comment: String
)

data class Appointment(
    val id: Int,
    val studentRegNumber: String,
    val facultyEmpId: String,
    val timeSlot: String,
    val reason: String,
    val status: String
) 