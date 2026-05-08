package com.example.aipersonal

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val messageImageView: ImageView = view.findViewById(R.id.messageImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = messages[position]
        
        val context = holder.itemView.context
        val markwon = Markwon.create(context)
        
        // Handle Base64 Image
        if (chatMessage.imageBase64 != null) {
            try {
                val decodedBytes = Base64.decode(chatMessage.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                if (bitmap != null) {
                    holder.messageImageView.visibility = View.VISIBLE
                    holder.messageImageView.setImageBitmap(bitmap)
                } else {
                    holder.messageImageView.visibility = View.GONE
                }
            } catch (e: Exception) {
                holder.messageImageView.visibility = View.GONE
            }
        } else {
            holder.messageImageView.visibility = View.GONE
        }

        // Render markdown
        markwon.setMarkdown(holder.messageTextView, chatMessage.message)
        
        val container = holder.itemView.findViewById<View>(R.id.messageContainer)
        val params = container.layoutParams as ConstraintLayout.LayoutParams
        
        if (chatMessage.sender == "Anda") {
            container.setBackgroundResource(R.drawable.bg_message_user)
            holder.messageTextView.setTextColor(Color.WHITE)
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        } else {
            container.setBackgroundResource(R.drawable.bg_message_ai)
            holder.messageTextView.setTextColor(context.getColor(R.color.text_ai))
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
        }
        container.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}
