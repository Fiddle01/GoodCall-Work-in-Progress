package com.example.goodcall

import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var groupCode: String
    private lateinit var db: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var group: Group
    private lateinit var infoButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var sendTextButton: ImageView
    private lateinit var sendRandomButton: ImageView
    private lateinit var textMessageEditText: EditText
    private lateinit var sendTextText: TextView
    private lateinit var sendRandomText: TextView
    private lateinit var messageRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //Get group code from intent:
        val bundle = intent.extras
        infoButton = findViewById(R.id.infoButton)
        backButton = findViewById(R.id.backButton)
        sendTextButton = findViewById(R.id.messageImageView)
        sendTextText = findViewById(R.id.sendTextText)
        sendRandomButton = findViewById(R.id.randomSendImageView)
        sendRandomText = findViewById(R.id.sendRandomText)
        messageRecyclerView = findViewById(R.id.messages_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        messageRecyclerView.layoutManager = layoutManager

        auth = FirebaseAuth.getInstance()

        if(bundle?.get("GROUP_CODE") != null) {
            groupCode = bundle.get("GROUP_CODE") as String
        }

        //Get group from database
        db = Firebase.database
        ref = db.reference
        ref.child(groupCode).get().addOnSuccessListener {
            if(it.getValue<Group>() != null) {
                group = it.getValue<Group>()!!
                //Apply group information:
                val groupName: TextView = findViewById(R.id.chat_group_name_text)
                groupName.text = group.name
            } else {
                Toast.makeText(this, "Failed to retrieve group information, please try again.", Toast.LENGTH_SHORT).show()
            }
         }

        //Set up buttons:

        //Add functionality to info button:
        infoButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Group code:")
                .setMessage(groupCode)
                .apply {
                    setPositiveButton(R.string.copy, DialogInterface.OnClickListener { _, _ ->
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("label", groupCode)
                        clipboard.setPrimaryClip(clip)
                    })
                    setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->
                        //do nothing
                    })
                }
            builder.create()
            builder.show()
            Log.d(TAG, "onCreate: Dialog created")
        }

        //Add functionality to the back button:
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //Add functionality to send message button:
        sendTextButton.setOnClickListener {
            sendMessageAlert()
        }
        sendTextText.setOnClickListener {
            sendMessageAlert()
        }

        //Add functionality to the send random event button:
        sendRandomText.setOnClickListener {
            sendRandomAlert()
        }
        sendRandomButton.setOnClickListener {
            sendRandomAlert()
        }

        //Get messages from the group and display them in the recycler view:
        val messageQuery = ref.child(groupCode).orderByKey().limitToFirst(50)

        messageQuery.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()

                for (message in snapshot.children) {
                    if (message?.value is HashMap<*, *>){
                        val textMessage = message.getValue<TextMessage>()

                        if (textMessage != null) {
                            messages.add(textMessage)
                            Log.d(TAG, "onDataChange: Test ${textMessage.contentsToString()}")
                        }
                    }
                }
                val adapter = MessageAdapter(messages)
                messageRecyclerView.adapter = adapter
                messageRecyclerView.scrollToPosition(messages.size-1)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: failed")
            }
        })

        }
    private fun sendMessageAlert() {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_send_text, null)

        builder.setView(dialogLayout)
            .setPositiveButton(R.string.send, DialogInterface.OnClickListener { _, _ ->
                textMessageEditText = dialogLayout.findViewById(R.id.textMessage)
                val text = textMessageEditText.text
                if(text.isNotBlank()) {
                    val textMessage = TextMessage(auth.currentUser!!.displayName!!, Date(), text.toString())
                    val key = ref.child(groupCode).push().key
                    ref.child(groupCode).child(key!!).setValue(textMessage)
                } else {
                    textMessageEditText.error = "You can't send a blank text message."
                }
            })
            .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->
                //do nothing
            })
            .show()

    }

    private fun sendRandomAlert() {
        val builder = AlertDialog.Builder(this)
        builder.apply {

        }
    }
}
