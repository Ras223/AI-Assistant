package com.example.aipersonal

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var topicAdapter: TopicAdapter
    private val sessions = mutableListOf<ChatSession>()
    private var currentSession: ChatSession? = null
    
    private val scope = MainScope()
    private val apiKey = "YOUR API KEY NVIDIA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val chatRecyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        val topicRecyclerView: RecyclerView = findViewById(R.id.topicRecyclerView)
        val queryEditText: EditText = findViewById(R.id.queryEditText)
        val sendButton: ImageView = findViewById(R.id.sendButton)
        val btnNewChat: Button = findViewById(R.id.btnNewChat)
        val nexusOrb: ImageView = findViewById(R.id.nexusOrb)
        val pillContainer: LinearLayout = findViewById(R.id.pillContainer)

        chatAdapter = ChatAdapter(mutableListOf())
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        topicAdapter = TopicAdapter(sessions) { selectedSession ->
            switchSession(selectedSession)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        topicRecyclerView.layoutManager = LinearLayoutManager(this)
        topicRecyclerView.adapter = topicAdapter

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        btnNewChat.setOnClickListener {
            createNewSession()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        pillContainer.apply {
            val drawPill = getChildAt(0) as TextView
            drawPill.setOnClickListener {
                queryEditText.setText("/draw ")
                queryEditText.setSelection(queryEditText.text.length)
            }
        }

        sendButton.setOnClickListener {
            val query = queryEditText.text.toString()
            if (query.isNotBlank()) {
                if (currentSession == null) createNewSession()
                
                val session = currentSession!!

                if (session.messages.isEmpty()) {
                    nexusOrb.visibility = View.GONE
                    pillContainer.visibility = View.GONE
                    session.title = if (query.length > 20) query.take(20) + "..." else query
                    topicAdapter.notifyDataSetChanged()
                }

                addMessageToUI("Anda", query)
                queryEditText.text.clear()
                
                processNexusCommand(session, query)
            }
        }

        queryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendButton.performClick()
                true
            } else {
                false
            }
        }

        createNewSession()
    }

    private fun createNewSession() {
        val newSession = ChatSession()
        sessions.add(0, newSession)
        topicAdapter.notifyDataSetChanged()
        switchSession(newSession)
        
        findViewById<ImageView>(R.id.nexusOrb).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.pillContainer).visibility = View.VISIBLE
    }

    private fun switchSession(session: ChatSession) {
        currentSession = session
        chatAdapter = ChatAdapter(session.messages)
        findViewById<RecyclerView>(R.id.chatRecyclerView).adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()
        
        val isEmpty = session.messages.isEmpty()
        findViewById<ImageView>(R.id.nexusOrb).visibility = if (isEmpty) View.VISIBLE else View.GONE
        findViewById<LinearLayout>(R.id.pillContainer).visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun processNexusCommand(session: ChatSession, query: String) {
        if (query.lowercase().startsWith("/draw")) {
            val prompt = query.removePrefix("/draw").trim()
            if (prompt.isNotEmpty()) {
                generateImage(prompt)
            } else {
                addMessageToUI("Nexus", "Silakan masukkan deskripsi gambar setelah /draw. Contoh: /draw futuristic city")
            }
        } else {
            generateTextResponse(session)
        }
    }

    private fun generateTextResponse(session: ChatSession) {
        scope.launch {
            try {
                val history = mutableListOf<Message>()
                history.add(
                    Message(
                        role = "system",
                        content = "You are Nexus, a futuristic and helpful AI assistant. " +
                                "Gunakan format Markdown untuk memberikan jawaban yang terstruktur. " +
                                "Jika user ingin membuat gambar, beri tahu mereka untuk menggunakan perintah /draw."
                    )
                )

                history.addAll(session.messages.map { 
                    Message(
                        role = if (it.sender == "Anda") "user" else "assistant",
                        content = it.message
                    )
                })
                
                val responseText = NvidiaClient.generateResponse(apiKey, history) 
                    ?: "Nexus could not reach the server."

                addMessageToUI("Nexus", responseText)
            } catch (e: Exception) {
                addMessageToUI("Error", e.localizedMessage ?: "Unknown system failure.")
            }
        }
    }

    private fun generateImage(prompt: String) {
        scope.launch {
            try {
                addMessageToUI("Nexus", "Sedang membuat gambar untuk: *$prompt*...")
                
                val imageBase64 = NvidiaClient.generateImage(apiKey, prompt)
                
                if (imageBase64 != null) {
                    addMessageWithImage("Nexus", "Berikut adalah gambar hasil kreasi saya:", imageBase64)
                } else {
                    addMessageToUI("Nexus", "Maaf, Nexus gagal membuat gambar tersebut. Coba prompt yang berbeda.")
                }
            } catch (e: Exception) {
                addMessageToUI("Error", "Gagal memproses gambar: ${e.localizedMessage}")
            }
        }
    }

    private fun addMessageToUI(sender: String, message: String) {
        val chatMsg = ChatMessage(sender, message)
        currentSession?.messages?.add(chatMsg)
        chatAdapter.notifyItemInserted((currentSession?.messages?.size ?: 1) - 1)
        findViewById<RecyclerView>(R.id.chatRecyclerView).scrollToPosition((currentSession?.messages?.size ?: 1) - 1)
    }

    private fun addMessageWithImage(sender: String, message: String, imageBase64: String) {
        val chatMsg = ChatMessage(sender, message, imageBase64)
        currentSession?.messages?.add(chatMsg)
        chatAdapter.notifyItemInserted((currentSession?.messages?.size ?: 1) - 1)
        findViewById<RecyclerView>(R.id.chatRecyclerView).scrollToPosition((currentSession?.messages?.size ?: 1) - 1)
    }
}
