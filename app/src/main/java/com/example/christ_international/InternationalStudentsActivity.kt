package com.example.christ_international

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class InternationalStudentsActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var schoolSpinner: AutoCompleteTextView
    private lateinit var departmentSpinner: AutoCompleteTextView
    private lateinit var departmentLayout: TextInputLayout
    private lateinit var etRegisterNumber: TextInputEditText
    private lateinit var etName: TextInputEditText
    private lateinit var btnSearch: MaterialButton
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private val schoolDepartments = mapOf(
        "School of Science" to listOf(
            "Computer Science",
            "Life Sciences",
            "Statistics",
            "Mathematics",
            "Physics",
            "Chemistry"
        ),
        "School of Commerce, Finance and Accountancy" to listOf(
            "Commerce",
            "Finance",
            "Accountancy",
            "Economics"
        ),
        "School of Law" to listOf(
            "Civil Law",
            "Criminal Law",
            "Corporate Law",
            "International Law"
        ),
        "School of Social Sciences" to listOf(
            "Sociology",
            "Political Science",
            "History",
            "Geography"
        ),
        "School of Arts and Humanities" to listOf(
            "English",
            "Literature",
            "Philosophy",
            "Fine Arts"
        ),
        "School of Education" to listOf(
            "Primary Education",
            "Secondary Education",
            "Special Education",
            "Educational Psychology"
        ),
        "School of Psychological Sciences" to listOf(
            "Clinical Psychology",
            "Counseling Psychology",
            "Organizational Psychology",
            "Child Psychology"
        ),
        "School of Business and Management" to listOf(
            "Business Administration",
            "Marketing",
            "Human Resource Management",
            "Operations Management"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_international_students)

        try {
            // Initialize Firebase
            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase.getReference("international_students")

            // Initialize database helper
            dbHelper = DatabaseHelper(this)

            // Setup toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = "International Students"
            }

            // Initialize views and adapters
            initializeViews()
            setupSchoolSpinner()
            setupSearchButton()
            setupTextWatchers()
            setupRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewStudents)
        schoolSpinner = findViewById(R.id.schoolSpinner)
        departmentSpinner = findViewById(R.id.departmentSpinner)
        departmentLayout = findViewById(R.id.departmentLayout)
        etRegisterNumber = findViewById(R.id.etRegisterNumber)
        etName = findViewById(R.id.etName)
        btnSearch = findViewById(R.id.btnSearch)

        // Initially hide department spinner
        departmentLayout.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@InternationalStudentsActivity)
            adapter = studentAdapter
        }
    }

    private fun setupTextWatchers() {
        // When register number is entered, disable name/school/department fields
        etRegisterNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasRegisterNumber = !s.isNullOrEmpty()
                etName.isEnabled = !hasRegisterNumber
                schoolSpinner.isEnabled = !hasRegisterNumber
                departmentSpinner.isEnabled = !hasRegisterNumber
            }
        })

        // When name is entered, disable register number
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasName = !s.isNullOrEmpty()
                etRegisterNumber.isEnabled = !hasName
            }
        })
    }

    private fun setupSchoolSpinner() {
        val schools = schoolDepartments.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, schools)
        schoolSpinner.setAdapter(adapter)

        schoolSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedSchool = schools[position]
            updateDepartmentSpinner(selectedSchool)
            etRegisterNumber.isEnabled = false
        }
    }

    private fun updateDepartmentSpinner(school: String) {
        val departments = schoolDepartments[school] ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, departments)
        departmentSpinner.setAdapter(adapter)
        departmentLayout.visibility = View.VISIBLE
        departmentSpinner.text.clear()
    }

    private fun setupSearchButton() {
        btnSearch.setOnClickListener {
            val registerNumber = etRegisterNumber.text.toString()
            val name = etName.text.toString()
            val school = schoolSpinner.text.toString()
            val department = departmentSpinner.text.toString()

            when {
                registerNumber.isNotEmpty() -> {
                    searchByRegisterNumber(registerNumber)
                }
                name.isNotEmpty() && school.isNotEmpty() -> {
                    if (department.isNotEmpty()) {
                        searchByNameAndDepartment(name, school, department)
                    } else {
                        searchByNameAndSchool(name, school)
                    }
                }
                else -> Toast.makeText(this, "Please enter either Register Number OR Name with School", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchByRegisterNumber(registerNumber: String) {
        databaseReference.orderByChild("registerNumber")
            .equalTo(registerNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val students = mutableListOf<Student>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(Student::class.java)?.let {
                            students.add(it)
                        }
                    }
                    updateResults(students)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@InternationalStudentsActivity, 
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun searchByNameAndSchool(name: String, school: String) {
        databaseReference.orderByChild("school")
            .equalTo(school)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val students = mutableListOf<Student>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(Student::class.java)?.let {
                            if (it.name.contains(name, ignoreCase = true)) {
                                students.add(it)
                            }
                        }
                    }
                    updateResults(students)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@InternationalStudentsActivity, 
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun searchByNameAndDepartment(name: String, school: String, department: String) {
        databaseReference.orderByChild("school")
            .equalTo(school)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val students = mutableListOf<Student>()
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(Student::class.java)?.let {
                            if (it.name.contains(name, ignoreCase = true) && 
                                it.department == department) {
                                students.add(it)
                            }
                        }
                    }
                    updateResults(students)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@InternationalStudentsActivity, 
                        "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateResults(students: List<Student>) {
        if (students.isEmpty()) {
            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show()
        }
        studentAdapter.updateStudents(students)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            dbHelper.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 