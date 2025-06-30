package com.example.christ_international

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ChristInternational.db"
        private const val DATABASE_VERSION = 1

        // Faculty table
        private const val TABLE_FACULTY = "faculty"
        private const val FACULTY_ID = "id"
        private const val FACULTY_EMP_ID = "emp_id"
        private const val FACULTY_NAME = "name"
        private const val FACULTY_EMAIL = "email"
        private const val FACULTY_PASSWORD = "password"
        private const val FACULTY_DEPARTMENT = "department"

        // Student table
        private const val TABLE_STUDENTS = "students"
        private const val STUDENT_ID = "id"
        private const val STUDENT_REG_NO = "reg_no"
        private const val STUDENT_NAME = "name"
        private const val STUDENT_EMAIL = "email"
        private const val STUDENT_PASSWORD = "password"
        private const val STUDENT_DEPARTMENT = "department"
        private const val STUDENT_COURSE = "course"

        // Events table
        private const val TABLE_EVENTS = "events"
        private const val EVENT_ID = "event_id"
        private const val EVENT_TITLE = "title"
        private const val EVENT_DESCRIPTION = "description"
        private const val EVENT_DATE = "date"
        private const val EVENT_TIME = "time"
        private const val EVENT_IMAGE_URL = "image_url"
        private const val EVENT_REGISTRATION_LINK = "registration_link"
        private const val EVENT_CREATED_AT = "created_at"

        // Appointments table
        private const val TABLE_APPOINTMENTS = "appointments"
        private const val APPOINTMENT_ID = "appointment_id"
        private const val TIME_SLOT = "time_slot"
        private const val REASON = "reason"
        private const val STATUS = "status"
        private const val CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create faculty table
        val createFacultyTable = """
            CREATE TABLE $TABLE_FACULTY (
                $FACULTY_ID TEXT PRIMARY KEY,
                $FACULTY_EMP_ID TEXT UNIQUE,
                $FACULTY_NAME TEXT,
                $FACULTY_EMAIL TEXT UNIQUE,
                $FACULTY_PASSWORD TEXT,
                $FACULTY_DEPARTMENT TEXT
            )
        """.trimIndent()

        // Create students table
        val createStudentTable = """
            CREATE TABLE $TABLE_STUDENTS (
                $STUDENT_ID TEXT PRIMARY KEY,
                $STUDENT_REG_NO TEXT UNIQUE,
                $STUDENT_NAME TEXT,
                $STUDENT_EMAIL TEXT UNIQUE,
                $STUDENT_PASSWORD TEXT,
                $STUDENT_DEPARTMENT TEXT,
                $STUDENT_COURSE TEXT
            )
        """.trimIndent()

        // Create events table
        val createEventsTable = """
            CREATE TABLE $TABLE_EVENTS (
                $EVENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $EVENT_TITLE TEXT,
                $EVENT_DESCRIPTION TEXT,
                $EVENT_DATE TEXT,
                $EVENT_TIME TEXT,
                $EVENT_IMAGE_URL TEXT,
                $EVENT_REGISTRATION_LINK TEXT,
                $EVENT_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Create appointments table
        val createAppointmentsTable = """
            CREATE TABLE $TABLE_APPOINTMENTS (
                $APPOINTMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_REG_NO TEXT NOT NULL,
                $FACULTY_EMP_ID TEXT NOT NULL,
                $TIME_SLOT TEXT NOT NULL,
                $REASON TEXT NOT NULL,
                $STATUS TEXT NOT NULL DEFAULT 'PENDING',
                $CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($STUDENT_REG_NO) REFERENCES $TABLE_STUDENTS($STUDENT_REG_NO),
                FOREIGN KEY($FACULTY_EMP_ID) REFERENCES $TABLE_FACULTY($FACULTY_EMP_ID)
            )
        """.trimIndent()

        // Create faculty_feedback table
        val createFeedbackTable = """
            CREATE TABLE faculty_feedback (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                faculty_emp_id TEXT NOT NULL,
                student_reg_no TEXT NOT NULL,
                rating INTEGER NOT NULL,
                comment TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(faculty_emp_id) REFERENCES $TABLE_FACULTY($FACULTY_EMP_ID),
                FOREIGN KEY(student_reg_no) REFERENCES $TABLE_STUDENTS($STUDENT_REG_NO)
            )
        """.trimIndent()

        db.execSQL(createFacultyTable)
        db.execSQL(createStudentTable)
        db.execSQL(createEventsTable)
        db.execSQL(createAppointmentsTable)
        db.execSQL(createFeedbackTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS faculty_feedback")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FACULTY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    // Faculty CRUD operations
    fun addFaculty(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(FACULTY_ID, user.id)
            put(FACULTY_EMP_ID, user.empId)
            put(FACULTY_NAME, user.name)
            put(FACULTY_EMAIL, user.email)
            put(FACULTY_PASSWORD, user.password)
            put(FACULTY_DEPARTMENT, user.department)
        }
        return db.insert(TABLE_FACULTY, null, values)
    }

    fun updateFaculty(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(FACULTY_NAME, user.name)
            put(FACULTY_EMAIL, user.email)
            put(FACULTY_PASSWORD, user.password)
            put(FACULTY_DEPARTMENT, user.department)
        }
        return db.update(TABLE_FACULTY, values, "$FACULTY_ID = ?", arrayOf(user.id))
    }

    fun deleteFaculty(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_FACULTY, "$FACULTY_ID = ?", arrayOf(id))
    }

    fun getAllFaculty(): List<User> {
        val facultyList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FACULTY,
            null,
            null,
            null,
            null,
            null,
            "$FACULTY_NAME ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val user = User(
                    id = getString(getColumnIndexOrThrow(FACULTY_ID)),
                    empId = getString(getColumnIndexOrThrow(FACULTY_EMP_ID)),
                    name = getString(getColumnIndexOrThrow(FACULTY_NAME)),
                    email = getString(getColumnIndexOrThrow(FACULTY_EMAIL)),
                    password = getString(getColumnIndexOrThrow(FACULTY_PASSWORD)),
                    type = UserType.FACULTY,
                    department = getString(getColumnIndexOrThrow(FACULTY_DEPARTMENT)),
                    course = null
                )
                facultyList.add(user)
            }
        }
        cursor.close()
        return facultyList
    }

    // Student CRUD operations
    fun addStudent(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(STUDENT_ID, user.id)
            put(STUDENT_REG_NO, user.regNumber)
            put(STUDENT_NAME, user.name)
            put(STUDENT_EMAIL, user.email)
            put(STUDENT_PASSWORD, user.password)
            put(STUDENT_DEPARTMENT, user.department)
            put(STUDENT_COURSE, user.course)
        }
        return db.insert(TABLE_STUDENTS, null, values)
    }

    fun updateStudent(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(STUDENT_NAME, user.name)
            put(STUDENT_EMAIL, user.email)
            put(STUDENT_PASSWORD, user.password)
            put(STUDENT_DEPARTMENT, user.department)
            put(STUDENT_COURSE, user.course)
        }
        return db.update(TABLE_STUDENTS, values, "$STUDENT_ID = ?", arrayOf(user.id))
    }

    fun deleteStudent(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_STUDENTS, "$STUDENT_ID = ?", arrayOf(id))
    }

    fun getAllStudents(): List<User> {
        val studentList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            null,
            null,
            null,
            null,
            null,
            "$STUDENT_NAME ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val user = User(
                    id = getString(getColumnIndexOrThrow(STUDENT_ID)),
                    regNumber = getString(getColumnIndexOrThrow(STUDENT_REG_NO)),
                    name = getString(getColumnIndexOrThrow(STUDENT_NAME)),
                    email = getString(getColumnIndexOrThrow(STUDENT_EMAIL)),
                    password = getString(getColumnIndexOrThrow(STUDENT_PASSWORD)),
                    type = UserType.STUDENT,
                    department = getString(getColumnIndexOrThrow(STUDENT_DEPARTMENT)),
                    course = getString(getColumnIndexOrThrow(STUDENT_COURSE))
                )
                studentList.add(user)
            }
        }
        cursor.close()
        return studentList
    }

    // Additional helper methods
    fun getStudentName(regNo: String): String? {
        val db = this.readableDatabase
        var name: String? = null
        
        try {
            val cursor = db.query(
                TABLE_STUDENTS,
                arrayOf(STUDENT_NAME),
                "$STUDENT_REG_NO = ?",
                arrayOf(regNo),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    name = it.getString(it.getColumnIndexOrThrow(STUDENT_NAME))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return name
    }

    fun getFacultyName(empId: String): String? {
        val db = this.readableDatabase
        var name: String? = null
        
        try {
            val cursor = db.query(
                TABLE_FACULTY,
                arrayOf(FACULTY_NAME),
                "$FACULTY_EMP_ID = ?",
                arrayOf(empId),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    name = it.getString(it.getColumnIndexOrThrow(FACULTY_NAME))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return name
    }

    fun authenticateStudent(regNo: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_STUDENTS,
            arrayOf(STUDENT_ID),
            "$STUDENT_REG_NO = ? AND $STUDENT_PASSWORD = ?",
            arrayOf(regNo, password),
            null,
            null,
            null
        )
        val result = cursor.count > 0
        cursor.close()
        return result
    }

    fun authenticateFaculty(empId: String, email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FACULTY,
            arrayOf(FACULTY_ID),
            "$FACULTY_EMP_ID = ? AND $FACULTY_EMAIL = ?",
            arrayOf(empId, email),
            null,
            null,
            null
        )
        val result = cursor.count > 0
        cursor.close()
        return result
    }

    // Appointment related methods
    fun addAppointment(studentRegNumber: String, facultyEmpId: String, timeSlot: String, reason: String): Long {
        return try {
            val db = this.writableDatabase
            
            // Check if the student already has an appointment with this faculty at this time slot
            val existingAppointmentCursor = db.query(
                TABLE_APPOINTMENTS,
                null,
                "$STUDENT_REG_NO = ? AND $FACULTY_EMP_ID = ? AND $TIME_SLOT = ? AND $STATUS IN (?, ?)",
                arrayOf(studentRegNumber, facultyEmpId, timeSlot, "PENDING", "APPROVED"),
                null,
                null,
                null
            )
            
            if (existingAppointmentCursor.count > 0) {
                existingAppointmentCursor.close()
                return -2L // Special code for existing appointment with same faculty and time
            }
            existingAppointmentCursor.close()

            // Check if the faculty is available at this time slot
            if (!isTimeSlotAvailable(facultyEmpId, timeSlot)) {
                return -3L // Special code for unavailable time slot
            }

            val values = ContentValues().apply {
                put(STUDENT_REG_NO, studentRegNumber)
                put(FACULTY_EMP_ID, facultyEmpId)
                put(TIME_SLOT, timeSlot)
                put(REASON, reason)
                put(STATUS, "PENDING")
                put(CREATED_AT, System.currentTimeMillis())
            }
            db.insert(TABLE_APPOINTMENTS, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    fun isTimeSlotAvailable(facultyEmpId: String, timeSlot: String): Boolean {
        val db = this.readableDatabase
        try {
            val cursor = db.query(
                TABLE_APPOINTMENTS,
                null,
                "$FACULTY_EMP_ID = ? AND $TIME_SLOT = ? AND $STATUS IN (?, ?)",
                arrayOf(facultyEmpId, timeSlot, "PENDING", "APPROVED"),
                null,
                null,
                null
            )
            val isAvailable = cursor.count == 0
            cursor.close()
            return isAvailable
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun updateAppointmentStatus(appointmentId: Int, newStatus: String): Int {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(STATUS, newStatus.uppercase())
            }
            db.update(
                TABLE_APPOINTMENTS,
                values,
                "$APPOINTMENT_ID = ?",
                arrayOf(appointmentId.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun getAppointments(id: String, isStudent: Boolean): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val db = this.readableDatabase
        
        try {
            val selection = if (isStudent) "$STUDENT_REG_NO = ?" else "$FACULTY_EMP_ID = ?"
            val cursor = db.query(
                TABLE_APPOINTMENTS,
                null,
                selection,
                arrayOf(id),
                null,
                null,
                "$CREATED_AT DESC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    appointments.add(
                        Appointment(
                            id = it.getInt(it.getColumnIndexOrThrow(APPOINTMENT_ID)),
                            studentRegNumber = it.getString(it.getColumnIndexOrThrow(STUDENT_REG_NO)),
                            facultyEmpId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                            timeSlot = it.getString(it.getColumnIndexOrThrow(TIME_SLOT)),
                            reason = it.getString(it.getColumnIndexOrThrow(REASON)),
                            status = it.getString(it.getColumnIndexOrThrow(STATUS))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return appointments
    }

    fun getFacultyByEmpId(empId: String): User? {
        val db = this.readableDatabase
        var faculty: User? = null
        
        try {
            val cursor = db.query(
                TABLE_FACULTY,
                null,
                "$FACULTY_EMP_ID = ?",
                arrayOf(empId),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    faculty = User(
                        id = it.getString(it.getColumnIndexOrThrow(FACULTY_ID)),
                        empId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(FACULTY_NAME)),
                        email = it.getString(it.getColumnIndexOrThrow(FACULTY_EMAIL)),
                        password = it.getString(it.getColumnIndexOrThrow(FACULTY_PASSWORD)),
                        type = UserType.FACULTY,
                        department = it.getString(it.getColumnIndexOrThrow(FACULTY_DEPARTMENT)),
                        course = null
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return faculty
    }

    fun getStudentByRegNo(regNumber: String): User? {
        val db = this.readableDatabase
        var student: User? = null
        
        try {
            val cursor = db.query(
                TABLE_STUDENTS,
                null,
                "$STUDENT_REG_NO = ?",
                arrayOf(regNumber),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    student = User(
                        id = it.getString(it.getColumnIndexOrThrow(STUDENT_ID)),
                        regNumber = it.getString(it.getColumnIndexOrThrow(STUDENT_REG_NO)),
                        name = it.getString(it.getColumnIndexOrThrow(STUDENT_NAME)),
                        email = it.getString(it.getColumnIndexOrThrow(STUDENT_EMAIL)),
                        password = it.getString(it.getColumnIndexOrThrow(STUDENT_PASSWORD)),
                        type = UserType.STUDENT,
                        department = it.getString(it.getColumnIndexOrThrow(STUDENT_DEPARTMENT)),
                        course = it.getString(it.getColumnIndexOrThrow(STUDENT_COURSE))
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return student
    }

    // Add these methods after the existing ones
    
    fun getAllFacultyForAppointments(): List<Faculty> {
        val facultyList = mutableListOf<Faculty>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FACULTY,
            arrayOf(FACULTY_EMP_ID, FACULTY_NAME, FACULTY_EMAIL, FACULTY_DEPARTMENT),
            null,
            null,
            null,
            null,
            "$FACULTY_NAME ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                facultyList.add(
                    Faculty(
                        empId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(FACULTY_NAME)),
                        email = it.getString(it.getColumnIndexOrThrow(FACULTY_EMAIL)),
                        department = it.getString(it.getColumnIndexOrThrow(FACULTY_DEPARTMENT))
                    )
                )
            }
        }
        return facultyList
    }

    fun getFacultyDetails(empId: String): Faculty? {
        val db = this.readableDatabase
        var faculty: Faculty? = null
        
        try {
            val cursor = db.query(
                TABLE_FACULTY,
                arrayOf(FACULTY_EMP_ID, FACULTY_NAME, FACULTY_EMAIL, FACULTY_DEPARTMENT),
                "$FACULTY_EMP_ID = ?",
                arrayOf(empId),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    faculty = Faculty(
                        empId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(FACULTY_NAME)),
                        email = it.getString(it.getColumnIndexOrThrow(FACULTY_EMAIL)),
                        department = it.getString(it.getColumnIndexOrThrow(FACULTY_DEPARTMENT))
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return faculty
    }

    fun getStudentDetails(regNo: String): Student? {
        val db = this.readableDatabase
        var student: Student? = null
        
        try {
            val cursor = db.query(
                TABLE_STUDENTS,
                arrayOf(STUDENT_REG_NO, STUDENT_NAME, STUDENT_EMAIL, STUDENT_DEPARTMENT, STUDENT_COURSE),
                "$STUDENT_REG_NO = ?",
                arrayOf(regNo),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    student = Student(
                        regNumber = it.getString(it.getColumnIndexOrThrow(STUDENT_REG_NO)),
                        name = it.getString(it.getColumnIndexOrThrow(STUDENT_NAME)),
                        email = it.getString(it.getColumnIndexOrThrow(STUDENT_EMAIL)),
                        department = it.getString(it.getColumnIndexOrThrow(STUDENT_DEPARTMENT)),
                        course = it.getString(it.getColumnIndexOrThrow(STUDENT_COURSE))
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return student
    }

    // Add these methods after the existing appointment methods
    
    fun getStudentAppointments(studentRegNo: String): List<Appointment> {
        return getAppointments(studentRegNo, true)
    }

    fun getFacultyAppointments(facultyEmpId: String): List<Appointment> {
        return getAppointments(facultyEmpId, false)
    }

    fun getAppointmentsByStatus(id: String, isStudent: Boolean, status: String): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val db = this.readableDatabase
        
        try {
            val selection = if (isStudent) 
                "$STUDENT_REG_NO = ? AND $STATUS = ?" 
            else 
                "$FACULTY_EMP_ID = ? AND $STATUS = ?"
            
            val cursor = db.query(
                TABLE_APPOINTMENTS,
                null,
                selection,
                arrayOf(id, status.uppercase()),
                null,
                null,
                "$CREATED_AT DESC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    appointments.add(
                        Appointment(
                            id = it.getInt(it.getColumnIndexOrThrow(APPOINTMENT_ID)),
                            studentRegNumber = it.getString(it.getColumnIndexOrThrow(STUDENT_REG_NO)),
                            facultyEmpId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                            timeSlot = it.getString(it.getColumnIndexOrThrow(TIME_SLOT)),
                            reason = it.getString(it.getColumnIndexOrThrow(REASON)),
                            status = it.getString(it.getColumnIndexOrThrow(STATUS))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return appointments
    }

    fun getStudentAppointmentsByStatus(studentRegNo: String, status: String): List<Appointment> {
        return getAppointmentsByStatus(studentRegNo, true, status)
    }

    fun getFacultyAppointmentsByStatus(facultyEmpId: String, status: String): List<Appointment> {
        return getAppointmentsByStatus(facultyEmpId, false, status)
    }

    fun getAppointmentById(appointmentId: Int): Appointment? {
        val db = this.readableDatabase
        var appointment: Appointment? = null
        
        try {
            val cursor = db.query(
                TABLE_APPOINTMENTS,
                null,
                "$APPOINTMENT_ID = ?",
                arrayOf(appointmentId.toString()),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    appointment = Appointment(
                        id = it.getInt(it.getColumnIndexOrThrow(APPOINTMENT_ID)),
                        studentRegNumber = it.getString(it.getColumnIndexOrThrow(STUDENT_REG_NO)),
                        facultyEmpId = it.getString(it.getColumnIndexOrThrow(FACULTY_EMP_ID)),
                        timeSlot = it.getString(it.getColumnIndexOrThrow(TIME_SLOT)),
                        reason = it.getString(it.getColumnIndexOrThrow(REASON)),
                        status = it.getString(it.getColumnIndexOrThrow(STATUS))
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return appointment
    }

    fun deleteAppointment(appointmentId: Int): Boolean {
        return try {
            val db = this.writableDatabase
            val result = db.delete(
                TABLE_APPOINTMENTS,
                "$APPOINTMENT_ID = ?",
                arrayOf(appointmentId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    data class Feedback(
        val studentRegNumber: String,
        val rating: Int,
        val comment: String
    )

    fun getFacultyFeedback(facultyEmpId: String): List<Feedback> {
        val feedbackList = mutableListOf<Feedback>()
        val db = this.readableDatabase
        
        try {
            val cursor = db.query(
                "faculty_feedback",
                null,
                "faculty_emp_id = ?",
                arrayOf(facultyEmpId),
                null,
                null,
                "created_at DESC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    feedbackList.add(
                        Feedback(
                            studentRegNumber = it.getString(it.getColumnIndexOrThrow("student_reg_no")),
                            rating = it.getInt(it.getColumnIndexOrThrow("rating")),
                            comment = it.getString(it.getColumnIndexOrThrow("comment"))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return feedbackList
    }

    fun addFeedback(studentRegNumber: String, facultyEmpId: String, rating: Int, comment: String): Long {
        return try {
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put("student_reg_no", studentRegNumber)
                put("faculty_emp_id", facultyEmpId)
                put("rating", rating)
                put("comment", comment)
                put("created_at", System.currentTimeMillis())
            }
            db.insert("faculty_feedback", null, values)
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    // Event related functions
    fun addEvent(event: Event): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(EVENT_TITLE, event.title)
            put(EVENT_DESCRIPTION, event.description)
            put(EVENT_DATE, event.date)
            put(EVENT_TIME, event.time)
            put(EVENT_IMAGE_URL, event.imageUrl)
            put(EVENT_REGISTRATION_LINK, event.registrationLink)
        }
        return db.insert(TABLE_EVENTS, null, values)
    }

    fun updateEvent(event: Event): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(EVENT_TITLE, event.title)
            put(EVENT_DESCRIPTION, event.description)
            put(EVENT_DATE, event.date)
            put(EVENT_TIME, event.time)
            put(EVENT_IMAGE_URL, event.imageUrl)
            put(EVENT_REGISTRATION_LINK, event.registrationLink)
        }
        return db.update(TABLE_EVENTS, values, "$EVENT_ID = ?", arrayOf(event.id.toString())) > 0
    }

    fun deleteEvent(eventId: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_EVENTS, "$EVENT_ID = ?", arrayOf(eventId.toString())) > 0
    }

    fun getEvent(eventId: Int): Event? {
        val db = this.readableDatabase
        var event: Event? = null
        
        try {
            val cursor = db.query(
                TABLE_EVENTS,
                null,
                "$EVENT_ID = ?",
                arrayOf(eventId.toString()),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    event = Event(
                        id = it.getInt(it.getColumnIndexOrThrow(EVENT_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(EVENT_TITLE)),
                        description = it.getString(it.getColumnIndexOrThrow(EVENT_DESCRIPTION)),
                        date = it.getString(it.getColumnIndexOrThrow(EVENT_DATE)),
                        time = it.getString(it.getColumnIndexOrThrow(EVENT_TIME)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(EVENT_IMAGE_URL)),
                        registrationLink = it.getString(it.getColumnIndexOrThrow(EVENT_REGISTRATION_LINK))
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return event
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val db = this.readableDatabase
        
        try {
            val cursor = db.query(
                TABLE_EVENTS,
                null,
                null,
                null,
                null,
                null,
                "$EVENT_CREATED_AT DESC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    events.add(
                        Event(
                            id = it.getInt(it.getColumnIndexOrThrow(EVENT_ID)),
                            title = it.getString(it.getColumnIndexOrThrow(EVENT_TITLE)),
                            description = it.getString(it.getColumnIndexOrThrow(EVENT_DESCRIPTION)),
                            date = it.getString(it.getColumnIndexOrThrow(EVENT_DATE)),
                            time = it.getString(it.getColumnIndexOrThrow(EVENT_TIME)),
                            imageUrl = it.getString(it.getColumnIndexOrThrow(EVENT_IMAGE_URL)),
                            registrationLink = it.getString(it.getColumnIndexOrThrow(EVENT_REGISTRATION_LINK))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return events
    }
}