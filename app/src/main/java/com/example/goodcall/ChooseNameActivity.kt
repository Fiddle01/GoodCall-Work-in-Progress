package com.example.goodcall

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChooseNameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_name)

        //Initialize variables
        val displayNameEditText: EditText = findViewById(R.id.editTextDisplayName)
        val submitButton: Button = findViewById(R.id.submitNameButton)
        auth = FirebaseAuth.getInstance()
        db = Firebase.database

        //Handle dynamic button colors with text input:
        displayNameEditText.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrBlank()) {
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            } else {
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.vivid_tangerine))
            }
        }


        //Handle submitting display name
        submitButton.setOnClickListener {
            val name = displayNameEditText.text.toString()

            if(name.isNotBlank()) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Add blank user data to Firebase
                        val ref = db.reference
                        ref.child(auth.currentUser!!.uid).setValue(ArrayList<String>()) //empty array list will eventually contain group ID's the user is a part of.
                        //Start the next activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Task failed, try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                displayNameEditText.error = "Your display name needs to be at least 1 character long."
            }
        }
    }
}