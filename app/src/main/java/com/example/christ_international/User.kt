package com.example.christ_international

enum class UserType {
    FACULTY,
    STUDENT
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val type: UserType,
    val empId: String? = null,  // For faculty
    val regNumber: String? = null,  // For students
    val department: String? = null,
    val course: String? = null
) 