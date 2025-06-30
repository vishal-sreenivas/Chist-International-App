package com.example.christ_international.data

import androidx.room.*

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE regNumber = :regNumber")
    suspend fun getStudentByRegNumber(regNumber: String): Student?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :name || '%' AND level = :level AND program = :program")
    suspend fun searchStudents(name: String, level: String, program: String): List<Student>

    @Query("SELECT * FROM students WHERE level = :level AND program = :program")
    suspend fun searchStudentsByLevelAndProgram(level: String, program: String): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)
}
