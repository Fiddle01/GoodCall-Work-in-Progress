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
import kotlin.random.Random.Default.nextBoolean

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
    private lateinit var randomEvents: MutableList<RandomEvent>

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
        } else {
            groupCode = "nothing"
        }
        Log.d(TAG, "##onCreate: $groupCode")

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
            startActivity(Intent(this, MainActivity::class.java))
        }

        //Add functionality to send message button:
        sendTextButton.setOnClickListener {
            sendMessageAlert()
        }
        sendTextText.setOnClickListener {
            sendMessageAlert()
        }

        randomEvents = mutableListOf(
            RandomEvent("Coin flip", R.drawable.ic_baseline_heads_24),
            RandomEvent("Choose Random Member", R.drawable.ic_baseline_groups_2_24)
        )

        //Add functionality to the send random event button:
        sendRandomText.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater;
            val alertDialog = inflater.inflate(R.layout.dialog_send_random_event, null)
            val recyclerView = alertDialog.findViewById<RecyclerView>(R.id.random_select_recycler)
            recyclerView.adapter = RandomEventAdapter(randomEvents, groupCode)
            recyclerView.layoutManager = LinearLayoutManager(this)
            builder.setView(alertDialog)
            builder.show()
        }
        sendRandomButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater;
            val alertDialog = inflater.inflate(R.layout.dialog_send_random_event, null)
            val recyclerView = alertDialog.findViewById<RecyclerView>(R.id.random_select_recycler)
            recyclerView.adapter = RandomEventAdapter(randomEvents, groupCode)
            recyclerView.layoutManager = LinearLayoutManager(this)
            builder.setView(alertDialog)
            builder.show()
        }

        //Get messages from the group and display them in the recycler view:
        val messageQuery = ref.child(groupCode).orderByKey().limitToFirst(50)

        messageQuery.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (message in snapshot.children) {
                    if (message?.value is HashMap<*, *>){
                        val message1 = message.value as HashMap<String, Any?>
                        //TODO UPDATE when adding new event
                        if(message1["text"] != null) {
                            message.getValue(TextMessage::class.java)?.let {
                                messages.add(it)
                            }
                        } else if (message1["chosenMember"] != null) {
                            message.getValue(RandomMemberMessage::class.java)?.let {
                                messages.add(it)
                            }
                        } else {
                            //CoinMessage must be the last case since I can't use the != null trick on Boolean
                            //it seems using get value and passing in the class fixed it
                            //but before it didn't fix it, this may be inconsistent code.
                            message.getValue(CoinMessage::class.java)?.let {
                                Log.d(TAG, "onDataChange: isHeads: ${it.isHeads}")
                                messages.add(it)
                            }
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

    companion object {
        fun sendRandomEvent (randomEvent: RandomEvent, context: Context, gc: String) {
            val db = Firebase.database
            val auth = FirebaseAuth.getInstance()
            val username = auth.currentUser!!.displayName!!
            val ref = db.reference

            Log.d(TAG, "##sendRandomEvent: still alive")

            Log.d(TAG, "##sendRandomEvent: $gc")

            val key = ref.child(gc).push().key
            
            val members: ArrayList<String> = arrayListOf()
            var chosenMember: String
            
            ref.child(gc).child("members").get().addOnSuccessListener {
                for (member in it.children) {
                    members.add(member.value.toString())
                }
                Log.d(TAG, "Members in group: $members")
                val i = Math.floor(members.size * Math.random())
                chosenMember = members[i.toInt()]

                var value: Message = when(randomEvent.name) {
                    
                    //TODO when adding more random events update this when statement
                    "Coin flip" -> CoinMessage(username, Date(), nextBoolean())
                    "Choose Random Member" ->  {
                        Log.d(TAG, "sendRandomEvent: THIS HAPPENED")
                        Log.d(TAG, "sendRandomEvent: $chosenMember")
                        RandomMemberMessage(username, Date(), chosenMember)
                    }
                    else -> TextMessage(username, Date(), "")
                }

                //TODO this if chain also must be updated
                if(value is TextMessage) {
                    //This happens if the random event was not added to the switch statement
                    Log.d(TAG, "sendRandomEvent: VALUE IS TEXT MESSAGE")
                    Toast.makeText(context, "Sending random event failed", Toast.LENGTH_SHORT).show()
                } else if (value is CoinMessage || value is RandomMemberMessage) {
                    //TODO RandomMemberMessage is being written to Firebase incorrectly, it does not give chosen member
                    ref.child(gc).child(key!!).setValue(value)
                }
            }
        }
    }
}
