package com.example.goodcall

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter (private var groups: MutableList<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder> () {
    class ViewHolder internal constructor(itemView: View):
    RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.group_name)
        val memberText: TextView = itemView.findViewById(R.id.group_members)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_item, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = groups[position].name
        holder.nameText.typeface = Typeface.DEFAULT_BOLD
        holder.memberText.text = groups[position].members.toString().substring(1, groups[position].members.toString().length-1)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.nameText.context, ChatActivity::class.java)
            intent.putExtra("GROUP_CODE", groups[position].code)
            holder.nameText.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}