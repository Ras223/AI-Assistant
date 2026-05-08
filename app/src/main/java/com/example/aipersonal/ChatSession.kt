package com.example.aipersonal

data class ChatSession(
    val id: Long = System.currentTimeMillis(),
    var title: String = "Percakapan Baru",
    val messages: MutableList<ChatMessage> = mutableListOf()
)
