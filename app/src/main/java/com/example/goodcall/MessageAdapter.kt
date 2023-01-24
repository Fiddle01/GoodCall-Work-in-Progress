package com.example.goodcall

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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
        val randomDateText: TextView = itemView.findViewById(R.id.date_text_random_coin)
        val randomCardView: CardView = itemView.findViewById(R.id.your_random_card_view)
        val randomResultText: TextView = itemView.findViewById(R.id.random_result_text)
        val randomIcon: ImageView = itemView.findViewById(R.id.your_random_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false) as View
        auth = FirebaseAuth.getInstance()
        return MessageAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val username = auth.currentUser!!.displayName!!
        Log.d(TAG, "onBindViewHolder: here")
        Log.d(TAG, "is random member message: ${messages[position]} ")
        if (messages[position] is RandomMemberMessage)
        {
            Log.d(TAG, "RANDOM MEMBER MESSAGE FOUND")
            val randomMemberMessage = messages[position] as RandomMemberMessage
            //Initialize views to show result of random member choice:
            holder.randomDateText.text = randomMemberMessage.timeSent.dateToStandardTimeString()
            holder.randomResultText.text = randomMemberMessage.contentsToString()
            holder.randomIcon.setImageResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            holder.randomIcon.setBackgroundResource(R.drawable.ic_baseline_groups_2_24)
            holder.randomIcon.visibility = View.VISIBLE
            holder.randomCardView.visibility = View.VISIBLE

            //If you sent the random member choosing:
            if (randomMemberMessage.author == auth.currentUser!!.displayName) {
                //Align all views to end:
                val cardParams: RelativeLayout.LayoutParams = holder.randomCardView.layoutParams as RelativeLayout.LayoutParams
                cardParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomCardView.layoutParams = cardParams

                val textResultParams: RelativeLayout.LayoutParams = holder.randomResultText.layoutParams as RelativeLayout.LayoutParams
                textResultParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomResultText.layoutParams = textResultParams

                val textDateParams: RelativeLayout.LayoutParams = holder.randomDateText.layoutParams as RelativeLayout.LayoutParams
                textDateParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomDateText.layoutParams = textDateParams
            }
        }
        else if (messages[position] is CoinMessage && (messages[position] as CoinMessage).author != "NONE") {
            val coinMessage = messages[position] as CoinMessage
            //Initialize views to show results of the coin flip:
            Log.d(TAG, "onBindViewHolder: COIN MESSAGE FOUND")
            holder.randomDateText.text = coinMessage.timeSent.dateToStandardTimeString()
            holder.randomResultText.text = coinMessage.contentsToString()
            holder.randomIcon.setImageResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            val imageIcon = if (coinMessage.isHeads) R.drawable.ic_baseline_heads_24 else R.drawable.ic_baseline_tails_24
            holder.randomIcon.setBackgroundResource(imageIcon)
            holder.randomIcon.visibility = View.VISIBLE
            holder.randomCardView.visibility = View.VISIBLE

            //If you sent the coinflip:
            if (coinMessage.author == auth.currentUser!!.displayName) {
                //Align all views to end:
                val cardParams: RelativeLayout.LayoutParams = holder.randomCardView.layoutParams as RelativeLayout.LayoutParams
                cardParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomCardView.layoutParams = cardParams

                val textResultParams: RelativeLayout.LayoutParams = holder.randomResultText.layoutParams as RelativeLayout.LayoutParams
                textResultParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomResultText.layoutParams = textResultParams

                val textDateParams: RelativeLayout.LayoutParams = holder.randomDateText.layoutParams as RelativeLayout.LayoutParams
                textDateParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                holder.randomDateText.layoutParams = textDateParams
            }
        }
        else if(messages[position] is TextMessage) {
            Log.d(TAG, "onBindViewHolder: TEXT MESSAGE FOUND")
            if (username == (messages[position].author)) {
                holder.yourText.visibility = View.VISIBLE
                holder.randomDateText.visibility = View.VISIBLE
                holder.yourText.text = messages[position].contentsToString()
                holder.randomDateText.text = messages[position].timeSent.dateToStandardTimeString()
            } else {
                holder.otherText.visibility = View.VISIBLE
                holder.authorText.visibility = View.VISIBLE
                holder.dateText.visibility = View.VISIBLE
                holder.otherText.text = messages[position].contentsToString()
                holder.authorText.text = messages[position].author
                holder.dateText.text = messages[position].timeSent.dateToStandardTimeString()
            }
        }
    }

    //Extension functions are pretty sick so why not:
    private fun Date.dateToStandardTimeString() : String {
        var time = this.toString()
        Log.d(TAG, "dateToStandardTimeString: $time")
        time = time.substring(0, time.indexOf("T ") - 6)
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