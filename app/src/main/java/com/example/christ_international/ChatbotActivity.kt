package com.example.christ_international

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class ChatbotActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: MaterialButton
    private lateinit var chatAdapter: ChatAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Christ International Chatbot"

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        // Set up RecyclerView
        chatAdapter = ChatAdapter()
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        // Add welcome message
        addBotMessage("Hello! I'm your Christ International assistant. How can I help you today?")

        // Set up send button click listener
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Handle keyboard send button
        messageInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && 
                 event.keyCode == KeyEvent.KEYCODE_ENTER && 
                 event.action == KeyEvent.ACTION_DOWN)
            ) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val message = messageInput.text.toString().trim()
        if (message.isNotEmpty()) {
            // Add user message
            chatAdapter.addMessage(ChatMessage(message, false))
            messageInput.text.clear()

            // Scroll to bottom
            chatRecyclerView.post {
                chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }

            // Process message and get bot response
            processUserMessage(message)
        }
    }

    private fun processUserMessage(message: String) {
        coroutineScope.launch {
            try {
                // Simulate typing delay
                delay(1000)

                val response = when {
                    message.contains("hello", ignoreCase = true) || 
                    message.contains("hi", ignoreCase = true) -> 
                        "Hello! I'm your Christ International assistant. How can I help you today?\n\nYou can ask me about:\n" +
                        "1. Courses and Programs\n" +
                        "2. Admissions\n" +
                        "3. Faculty\n" +
                        "4. Facilities\n" +
                        "5. Placements\n" +
                        "6. Hostel"

                    message.contains("course", ignoreCase = true) || 
                    message.contains("program", ignoreCase = true) ->
                        "Christ International offers the following programs:\n\n" +
                        "Undergraduate Programs:\n" +
                        "• BCA (Bachelor of Computer Applications) - 3 years\n" +
                        "• BBA (Bachelor of Business Administration) - 3 years\n" +
                        "• B.Com (Bachelor of Commerce) - 3 years\n\n" +
                        "Postgraduate Programs:\n" +
                        "• MCA (Master of Computer Applications) - 2 years\n" +
                        "• MBA (Master of Business Administration) - 2 years\n" +
                        "• M.Sc AI-ML (Artificial Intelligence & Machine Learning) - 2 years\n\n" +
                        "Would you like specific details about any program?"

                    message.contains("admission", ignoreCase = true) ->
                        "Admission Process at Christ International:\n\n" +
                        "1. Eligibility:\n" +
                        "• UG Programs: 10+2 with minimum 50% marks\n" +
                        "• PG Programs: Bachelor's degree with 50% marks\n\n" +
                        "2. Application Process:\n" +
                        "• Online application through university portal\n" +
                        "• Application fee: ₹1000\n" +
                        "• Required documents submission\n\n" +
                        "3. Selection Process:\n" +
                        "• Entrance examination\n" +
                        "• Personal interview\n" +
                        "• Academic record evaluation\n\n" +
                        "For more details, visit our admission office or call: +91-XXX-XXXXXXX"

                    message.contains("faculty", ignoreCase = true) ->
                        "Our Faculty:\n\n" +
                        "1. Qualifications:\n" +
                        "• 80% PhD holders\n" +
                        "• Industry experts\n" +
                        "• Research scholars\n\n" +
                        "2. Departments:\n" +
                        "• Computer Science\n" +
                        "• Business Administration\n" +
                        "• Commerce\n" +
                        "• Mathematics\n" +
                        "• Languages\n\n" +
                        "3. Experience:\n" +
                        "• Average teaching experience: 12+ years\n" +
                        "• Industry experience: 5+ years\n\n" +
                        "Would you like to know about a specific department?"

                    message.contains("facility", ignoreCase = true) || 
                    message.contains("infrastructure", ignoreCase = true) ->
                        "Campus Facilities:\n\n" +
                        "1. Academic Facilities:\n" +
                        "• Smart classrooms with projectors\n" +
                        "• Computer labs with latest hardware\n" +
                        "• Digital library\n" +
                        "• Research centers\n\n" +
                        "2. Sports Facilities:\n" +
                        "• Indoor sports complex\n" +
                        "• Outdoor sports grounds\n" +
                        "• Fitness center\n\n" +
                        "3. Other Facilities:\n" +
                        "• Wi-Fi enabled campus\n" +
                        "• Modern cafeteria\n" +
                        "• Medical center\n" +
                        "• Transportation\n" +
                        "• Student activity center"

                    message.contains("placement", ignoreCase = true) || 
                    message.contains("job", ignoreCase = true) ->
                        "Placement Statistics:\n\n" +
                        "1. Overview:\n" +
                        "• Placement rate: 95%\n" +
                        "• Average package: ₹6.5 LPA\n" +
                        "• Highest package: ₹18 LPA\n\n" +
                        "2. Top Recruiters:\n" +
                        "• Microsoft\n" +
                        "• IBM\n" +
                        "• Accenture\n" +
                        "• TCS\n" +
                        "• Infosys\n\n" +
                        "3. Placement Support:\n" +
                        "• Pre-placement training\n" +
                        "• Mock interviews\n" +
                        "• Resume building workshops\n" +
                        "• Soft skills development"

                    message.contains("hostel", ignoreCase = true) || 
                    message.contains("accommodation", ignoreCase = true) ->
                        "Hostel Facilities:\n\n" +
                        "1. Infrastructure:\n" +
                        "• Separate blocks for boys and girls\n" +
                        "• AC and non-AC rooms available\n" +
                        "• 2/3 seater options\n\n" +
                        "2. Amenities:\n" +
                        "• 24/7 security with CCTV\n" +
                        "• Wi-Fi connectivity\n" +
                        "• Modern mess facility\n" +
                        "• Laundry service\n" +
                        "• Recreation rooms\n\n" +
                        "3. Fee Structure:\n" +
                        "• AC rooms: ₹1,20,000/year\n" +
                        "• Non-AC rooms: ₹80,000/year\n" +
                        "• Mess fee included"

                    message.contains("thank", ignoreCase = true) ->
                        "You're welcome! Feel free to ask if you have any other questions. I'm here to help! 😊"

                    else -> "I understand you're asking about ${message.lowercase()}. Could you please be more specific? You can ask about:\n" +
                           "1. Courses and Programs\n" +
                           "2. Admissions\n" +
                           "3. Faculty\n" +
                           "4. Facilities\n" +
                           "5. Placements\n" +
                           "6. Hostel"
                }

                addBotMessage(response)
            } catch (e: Exception) {
                addBotMessage("I apologize, but I'm having trouble processing your request. Please try again.")
            }
        }
    }

    private fun addBotMessage(message: String) {
        chatAdapter.addMessage(ChatMessage(message, true))
        chatRecyclerView.post {
            chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }
} 