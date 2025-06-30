package com.example.christ_international

data class ChatMessage(
    val message: String,
    val isBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) 