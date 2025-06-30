package com.example.christ_international

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.christ_international.data.AppDatabase
import com.example.christ_international.Student
import kotlinx.coroutines.launch

class InternationalStudentsActivity : AppCompatActivity() {

    private lateinit var etRegisterNumber: TextInputEditText
    private lateinit var etName: TextInputEditText
    private lateinit var spinnerLevel: AutoCompleteTextView
    private lateinit var spinnerProgram: AutoCompleteTextView
    private lateinit var btnSearch: MaterialButton
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_international_students)

        // Initialize database
        db = AppDatabase.getDatabase(this)

        // Initialize views
        etRegisterNumber = findViewById(R.id.etRegisterNumber)
        etName = findViewById(R.id.etName)
        spinnerLevel = findViewById(R.id.spinnerLevel)
        spinnerProgram = findViewById(R.id.spinnerProgram)
        btnSearch = findViewById(R.id.btnSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        // Setup spinners
        setupSpinners()

        // Setup RecyclerView
        adapter = StudentAdapter()
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = adapter

        // Setup search button click listener
        btnSearch.setOnClickListener {
            performSearch()
        }
    }

    private fun setupSpinners() {
        // Setup Level spinner
        val levels = arrayOf("Under Graduate Degree", "Post Graduate Degree")
        val levelAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, levels)
        spinnerLevel.setAdapter(levelAdapter)

        // Initially set program adapter with empty list
        var currentPrograms = emptyList<String>()
        val programAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, currentPrograms)
        spinnerProgram.setAdapter(programAdapter)

        // Update programs when level is selected
        spinnerLevel.setOnItemClickListener { _, _, position, _ ->
            currentPrograms = when (levels[position]) {
                "Under Graduate Degree" -> undergraduatePrograms
                "Post Graduate Degree" -> postgraduatePrograms
                else -> emptyList()
            }
            val newProgramAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, currentPrograms)
            spinnerProgram.setAdapter(newProgramAdapter)
            spinnerProgram.text.clear()
        }
    }

    private fun performSearch() {
        val registerNumber = etRegisterNumber.text.toString()
        val name = etName.text.toString()
        val level = spinnerLevel.text.toString()
        val program = spinnerProgram.text.toString()

        lifecycleScope.launch {
            val results = when {
                registerNumber.isNotEmpty() -> {
                    listOfNotNull(db.studentDao().getStudentByRegNumber(registerNumber))
                }
                name.isNotEmpty() -> {
                    db.studentDao().searchStudents(name, level, program)
                }
                else -> {
                    db.studentDao().searchStudentsByLevelAndProgram(level, program)
                }
            }
            adapter.updateStudents(results)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private val undergraduatePrograms = listOf(
            "Bachelor of Computer Applications",
            "Bachelor of Computer Applications (BCA) - Bangalore Central Campus",
            "Bachelor of Computer Applications (BCA) - Delhi NCR Campus",
            "BSc (Biotechnology, Chemistry) - Bangalore Central Campus",
            "BSc (Biotechnology, Zoology) - Bangalore Central Campus",
            "BSc (Chemistry, Zoology) - Bangalore Central Campus",
            "BSc (Computer Science, Mathematics) - Bangalore Central Campus",
            "BSc (Computer Science, Statistics) - Bangalore Central Campus",
            "BSc (Data Science, Mathematics) - Bangalore Central Campus",
            "BSc (Data Science, Statistics) - Bangalore Central Campus",
            "BSc (Economics and Analytics) - Pune Lavasa Campus",
            "BSc (Economics, Mathematics, Statistics) - Bangalore Central Campus",
            "BSc (Life Sciences) - Bangalore Central Campus",
            "BSc (Physics, Chemistry) - Bangalore Central Campus",
            "BSc (Physics, Mathematics) - Bangalore Central Campus",
            "BSc (Psychology) - Bangalore BGR Campus",
            "BSc (Psychology) - Bangalore Kengeri Campus",
            "BSc (Psychology) - Bangalore Yeshwanthpur Campus",
            "BSc in Biotechnology, Chemistry, Zoology",
            "BSc in Computer Science, Mathematics, Statistics",
            "BSc in Economics, Mathematics,Statistics",
            "BSc Psychology (Honours)",
            "BSc Psychology (Honours) - Bangalore Kengeri Campus"
        )

        private val postgraduatePrograms = listOf(
            "Master of Computer Applications",
            "Master of Science (Behavioural Science)",
            "Master of Science (Artificial Intelligence and Machine Learning)",
            "Master of Science (Data Science)",
            "Master of Science (Neuropsychology)",
            "Master of Science in Psychology (Clinical)",
            "Master of Science in Psychology (Clinical) - Delhi NCR Campus",
            "Master of Science in Psychology (Counselling)",
            "MSc (Computer Science and Applications)",
            "MSc (Statistics)"
        )
    }
}