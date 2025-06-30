package com.example.christ_international

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

class UserListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var userType: UserType
    private lateinit var dbHelper: DatabaseHelper

    companion object {
        private const val ARG_USER_TYPE = "user_type"

        fun newInstance(userType: UserType): UserListFragment {
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_USER_TYPE, userType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userType = arguments?.getSerializable(ARG_USER_TYPE) as UserType
        dbHelper = DatabaseHelper(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = UserAdapter(mutableListOf()) { user, action ->
            when (action) {
                UserAction.EDIT -> showEditUserDialog(user)
                UserAction.DELETE -> showDeleteConfirmationDialog(user)
            }
        }
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showAddUserDialog()
        }

        loadUsers()
    }

    private fun loadUsers() {
        val users = if (userType == UserType.FACULTY) {
            dbHelper.getAllFaculty()
        } else {
            dbHelper.getAllStudents()
        }
        adapter.updateUsers(users)
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_user, null)

        // Show/hide fields based on user type
        dialogView.findViewById<TextInputLayout>(R.id.courseLayout).visibility =
            if (userType == UserType.STUDENT) View.VISIBLE else View.GONE
        dialogView.findViewById<TextInputLayout>(R.id.empIdLayout).visibility =
            if (userType == UserType.FACULTY) View.VISIBLE else View.GONE
        dialogView.findViewById<TextInputLayout>(R.id.regNumberLayout).visibility =
            if (userType == UserType.STUDENT) View.VISIBLE else View.GONE

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add ${if (userType == UserType.FACULTY) "Faculty" else "Student"}")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = dialogView.findViewById<TextInputEditText>(R.id.etName).text.toString()
                val email = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString()
                val password = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString()
                val department = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString()
                val course = if (userType == UserType.STUDENT) {
                    dialogView.findViewById<TextInputEditText>(R.id.etCourse).text.toString()
                } else null
                val empId = if (userType == UserType.FACULTY) {
                    dialogView.findViewById<TextInputEditText>(R.id.etEmpId).text.toString()
                } else null
                val regNumber = if (userType == UserType.STUDENT) {
                    dialogView.findViewById<TextInputEditText>(R.id.etRegNumber).text.toString()
                } else null

                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    val newUser = User(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        email = email,
                        password = password,
                        type = userType,
                        department = department.takeIf { it.isNotBlank() },
                        course = course?.takeIf { it.isNotBlank() },
                        empId = empId?.takeIf { it.isNotBlank() },
                        regNumber = regNumber?.takeIf { it.isNotBlank() }
                    )
                    addUser(newUser)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditUserDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_user, null)

        // Show/hide fields based on user type
        dialogView.findViewById<TextInputLayout>(R.id.courseLayout).visibility =
            if (userType == UserType.STUDENT) View.VISIBLE else View.GONE
        dialogView.findViewById<TextInputLayout>(R.id.empIdLayout).visibility =
            if (userType == UserType.FACULTY) View.VISIBLE else View.GONE
        dialogView.findViewById<TextInputLayout>(R.id.regNumberLayout).visibility =
            if (userType == UserType.STUDENT) View.VISIBLE else View.GONE

        // Pre-fill existing data
        dialogView.findViewById<TextInputEditText>(R.id.etName).setText(user.name)
        dialogView.findViewById<TextInputEditText>(R.id.etEmail).setText(user.email)
        dialogView.findViewById<TextInputEditText>(R.id.etPassword).setText(user.password)
        dialogView.findViewById<TextInputEditText>(R.id.etDepartment).setText(user.department)
        if (userType == UserType.STUDENT) {
            dialogView.findViewById<TextInputEditText>(R.id.etCourse).setText(user.course)
            dialogView.findViewById<TextInputEditText>(R.id.etRegNumber).setText(user.regNumber)
        } else {
            dialogView.findViewById<TextInputEditText>(R.id.etEmpId).setText(user.empId)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit ${if (userType == UserType.FACULTY) "Faculty" else "Student"}")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val name = dialogView.findViewById<TextInputEditText>(R.id.etName).text.toString()
                val email = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString()
                val password = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString()
                val department = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString()
                val course = if (userType == UserType.STUDENT) {
                    dialogView.findViewById<TextInputEditText>(R.id.etCourse).text.toString()
                } else null
                val empId = if (userType == UserType.FACULTY) {
                    dialogView.findViewById<TextInputEditText>(R.id.etEmpId).text.toString()
                } else null
                val regNumber = if (userType == UserType.STUDENT) {
                    dialogView.findViewById<TextInputEditText>(R.id.etRegNumber).text.toString()
                } else null

                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    val updatedUser = user.copy(
                        name = name,
                        email = email,
                        password = password,
                        department = department.takeIf { it.isNotBlank() },
                        course = course?.takeIf { it.isNotBlank() },
                        empId = empId?.takeIf { it.isNotBlank() },
                        regNumber = regNumber?.takeIf { it.isNotBlank() }
                    )
                    updateUser(updatedUser)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addUser(user: User) {
        val result = if (user.type == UserType.FACULTY) {
            dbHelper.addFaculty(user)
        } else {
            dbHelper.addStudent(user)
        }
        if (result != -1L) {
            loadUsers() // Refresh the list
        }
    }

    private fun updateUser(user: User) {
        val result = if (user.type == UserType.FACULTY) {
            dbHelper.updateFaculty(user)
        } else {
            dbHelper.updateStudent(user)
        }
        if (result > 0) {
            loadUsers() // Refresh the list
        }
    }

    private fun deleteUser(user: User) {
        val result = if (user.type == UserType.FACULTY) {
            dbHelper.deleteFaculty(user.id)
        } else {
            dbHelper.deleteStudent(user.id)
        }
        if (result > 0) {
            loadUsers() // Refresh the list
        }
    }
}

enum class UserAction {
    EDIT,
    DELETE
}

class UserAdapter(
    private var users: MutableList<User>,
    private val onUserAction: (User, UserAction) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvEmpId: TextView = itemView.findViewById(R.id.tvEmpId)
        private val tvRegNumber: TextView = itemView.findViewById(R.id.tvRegNumber)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(user: User) {
            tvName.text = user.name
            tvEmail.text = user.email
            tvId.text = "ID: ${user.id}"

            // Show/hide and set empId for faculty
            if (user.type == UserType.FACULTY && !user.empId.isNullOrBlank()) {
                tvEmpId.visibility = View.VISIBLE
                tvEmpId.text = "Employee ID: ${user.empId}"
            } else {
                tvEmpId.visibility = View.GONE
            }

            // Show/hide and set regNumber for students
            if (user.type == UserType.STUDENT && !user.regNumber.isNullOrBlank()) {
                tvRegNumber.visibility = View.VISIBLE
                tvRegNumber.text = "Registration No: ${user.regNumber}"
            } else {
                tvRegNumber.visibility = View.GONE
            }

            btnEdit.setOnClickListener { onUserAction(user, UserAction.EDIT) }
            btnDelete.setOnClickListener { onUserAction(user, UserAction.DELETE) }
        }
    }
} 