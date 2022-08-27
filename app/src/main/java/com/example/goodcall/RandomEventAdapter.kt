package com.example.goodcall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RandomEventAdapter (private var randomEvents: MutableList<RandomEvent>, private val groupCode: String) : RecyclerView.Adapter<RandomEventAdapter.ViewHolder> () {
    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.random_item_name)
        val iconImage: ImageView = itemView.findViewById(R.id.random_item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.random_event_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = randomEvents[position].name
        holder.iconImage.setImageResource(randomEvents[position].icon)
        holder.itemView.setOnClickListener {
            ChatActivity.sendRandomEvent(randomEvents[position], holder.itemView.context, groupCode)
        }
    }

    override fun getItemCount(): Int {
        return randomEvents.size
    }
}