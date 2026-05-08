package com.example.aipersonal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopicAdapter(
    private val sessions: List<ChatSession>,
    private val onTopicClick: (ChatSession) -> Unit
) : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val topicTitle: TextView = view.findViewById(R.id.topicTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val session = sessions[position]
        holder.topicTitle.text = session.title
        holder.itemView.setOnClickListener { onTopicClick(session) }
    }

    override fun getItemCount(): Int = sessions.size
}
