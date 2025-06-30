package com.example.christ_international.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey
    val regNumber: String,
    val name: String,
    val email: String,
    val className: String,
    val program: String,
    val level: String,
    val campus: String,
    val nationality: String,
    val category: String
)
