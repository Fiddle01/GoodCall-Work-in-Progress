package com.example.goodcall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MessageAdapter (private var messages: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder> () {
    private lateinit var auth: FirebaseAuth
    class ViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
        val yourText: TextView = itemView.findViewById(R.id.yourTextView)
        val otherText: TextView = itemView.findViewById(R.id.otherTextView)
        val authorText: TextView = itemView.findViewById(R.id.authorText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val yourDateText: TextView = itemView.findViewById(R.id.your_date_text_random)
        val coinCardView: CardView = itemView.findViewById(R.id.your_random_card_view)
        val yourDateTextRandom: TextView = itemView.findViewById(R.id.your_date_text_random)
        val yourRandomResult: TextView = itemView.findViewById(R.id.your_random_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false) as View
        auth = FirebaseAuth.getInstance()
        return MessageAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false) //TODO eventually fix, this hurts performance of recycler view.
        val username = auth.currentUser!!.displayName!!
        if(messages[position] is TextMessage) {
            if (username == (messages[position].author)) {
                holder.yourText.visibility = View.VISIBLE
                holder.yourDateText.visibility = View.VISIBLE
                holder.yourText.text = messages[position].contentsToString()
                holder.yourDateText.text = messages[position].timeSent.dateToStandardTimeString()
            } else {
                holder.otherText.visibility = View.VISIBLE
                holder.authorText.visibility = View.VISIBLE
                holder.dateText.visibility = View.VISIBLE
                holder.otherText.text = messages[position].contentsToString()
                holder.authorText.text = messages[position].author
                holder.dateText.text = messages[position].timeSent.dateToStandardTimeString()
            }
        } // else if (messages[position] is CoinMessage... TODO)
        else if (messages[position] is CoinMessage) {
            holder.yourDateTextRandom.text = (messages[position] as CoinMessage).timeSent.dateToStandardTimeString()
            
        }
    }

    //Extension functions are pretty sick so why not:
    private fun Date.dateToStandardTimeString() : String {
        var time = this.toString()
        time = time.substring(0, time.indexOf("T") - 6)
        var hours = time.substring(time.indexOf(":") - 2, time.indexOf(":")).toInt()
        return if (hours > 12) {
            hours -= 12
            time = time.substring(
                0,
                time.indexOf(":") - 2
            ) + hours + time.substring(time.indexOf(":")) + "PM"
            time
        } else {
            time + "AM"
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}