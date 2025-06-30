package com.example.christ_international

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentLoginLayout: View
    private lateinit var facultyLoginLayout: View
    private lateinit var loginTypeRadioGroup: RadioGroup
    private lateinit var tvAdminLogin: TextView
    
    // Student views
    private lateinit var etStudentRegNo: TextInputEditText
    private lateinit var etStudentPassword: TextInputEditText
    
    // Faculty views
    private lateinit var etFacultyEmpId: TextInputEditText
    private lateinit var etFacultyEmail: TextInputEditText
    
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Initialize database
            dbHelper = DatabaseHelper(this)
            
            // Initialize views and setup listeners
            initializeViews()
            setupListeners()
            
            // Add initial data
            initializeData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeViews() {
        // Initialize layouts
        studentLoginLayout = findViewById(R.id.studentLoginLayout)
        facultyLoginLayout = findViewById(R.id.facultyLoginLayout)
        loginTypeRadioGroup = findViewById(R.id.loginTypeRadioGroup)
        tvAdminLogin = findViewById(R.id.tvAdminLogin)
        
        // Initialize student views
        etStudentRegNo = findViewById(R.id.etStudentRegNo)
        etStudentPassword = findViewById(R.id.etStudentPassword)
        
        // Initialize faculty views
        etFacultyEmpId = findViewById(R.id.etFacultyEmpId)
        etFacultyEmail = findViewById(R.id.etFacultyEmail)
        
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupListeners() {
        loginTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.studentRadioButton -> {
                    studentLoginLayout.visibility = View.VISIBLE
                    facultyLoginLayout.visibility = View.GONE
                }
                R.id.facultyRadioButton -> {
                    studentLoginLayout.visibility = View.GONE
                    facultyLoginLayout.visibility = View.VISIBLE
                }
            }
        }

        btnLogin.setOnClickListener {
            when (loginTypeRadioGroup.checkedRadioButtonId) {
                R.id.studentRadioButton -> handleStudentLogin()
                R.id.facultyRadioButton -> handleFacultyLogin()
            }
        }

        // Add click listener for admin login
        tvAdminLogin.setOnClickListener {
            try {
                val intent = Intent(this, AdminLoginActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleStudentLogin() {
        val regNumber = etStudentRegNo.text.toString().trim()
        val password = etStudentPassword.text.toString().trim()

        if (regNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.authenticateStudent(regNumber, password)) {
            // Navigate to StudentDashboardActivity
            val intent = Intent(this, StudentDashboardActivity::class.java).apply {
                putExtra("student_reg_number", regNumber)
            }
            startActivity(intent)
            finish() // Close login activity
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleFacultyLogin() {
        val empId = etFacultyEmpId.text.toString().trim()
        val email = etFacultyEmail.text.toString().trim()

        if (empId.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Debug: Print entered credentials
        println("Attempting login with - EmpID: $empId, Email: $email")

        if (dbHelper.authenticateFaculty(empId, email)) {
            // Get faculty details
            val facultyList = dbHelper.getAllFaculty()
            println("Found ${facultyList.size} faculty members") // Debug log
            
            facultyList.forEach { 
                println("Faculty in DB - EmpID: ${it.empId}, Email: ${it.email}") // Debug log
            }
            
            val faculty = facultyList.find { it.empId == empId }
            
            // Navigate to FacultyDashboardActivity and pass the Faculty details
            val intent = Intent(this, FacultyDashboardActivity::class.java).apply {
                putExtra("faculty_emp_id", empId)
                putExtra("faculty_name", faculty?.name ?: "Professor")
            }
            startActivity(intent)
            finish() // Close login activity
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeData() {
        try {
            // Check if faculty data already exists
            val existingFaculty = dbHelper.getAllFaculty()

            if (existingFaculty.isEmpty()) {
                // Add faculty members only if none exist
                val faculty1 = User(
                    id = "F1",
                    empId = "Emp1234",
                    name = "Prof. Vishal",
                    email = "vishal.s@mca.christuniversity.in",
                    password = "faculty123",
                    type = UserType.FACULTY,
                    department = "MCA",
                    course = null
                )
                val faculty2 = User(
                    id = "F2",
                    empId = "Emp1235",
                    name = "Prof. Sharanya",
                    email = "r.sharanya@mca.christuniversity.in",
                    password = "faculty123",
                    type = UserType.FACULTY,
                    department = "MCA",
                    course = null
                )
                val faculty3 = User(
                    id = "F3",
                    empId = "Emp1236",
                    name = "Prof. Vivek",
                    email = "vivek.s@mca.christuniversity.in",
                    password = "faculty123",
                    type = UserType.FACULTY,
                    department = "MCA",
                    course = null
                )

                dbHelper.addFaculty(faculty1)
                dbHelper.addFaculty(faculty2)
                dbHelper.addFaculty(faculty3)
            }

            // Add students
            val student1 = User(
                id = "S1",
                regNumber = "2447241",
                name = "R Sharanaya",
                email = "r.sharanaya@mca.christuniversity.in",
                password = "1427442",
                type = UserType.STUDENT,
                department = "MCA",
                course = "MCA"
            )
            val student2 = User(
                id = "S2",
                regNumber = "2447259",
                name = "Vishal S",
                email = "vishal.s@mca.christuniversity.in",
                password = "1234567",
                type = UserType.STUDENT,
                department = "MCA",
                course = "MCA"
            )
            val student3 = User(
                id = "S3",
                regNumber = "2447208",
                name = "Anjali K",
                email = "anjali.k@mca.christuniversity.in",
                password = "8027442",
                type = UserType.STUDENT,
                department = "MCA",
                course = "MCA"
            )

            dbHelper.addStudent(student1)
            dbHelper.addStudent(student2)
            dbHelper.addStudent(student3)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}