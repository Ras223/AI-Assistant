package com.example.aipersonal

data class ChatMessage(
    val sender: String,
    val message: String,
    val imageBase64: String? = null
)
