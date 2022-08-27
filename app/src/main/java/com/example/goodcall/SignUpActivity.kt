package com.example.goodcall

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        val emailEditText: EditText = findViewById(R.id.editTextDisplayName)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val submitButton: Button = findViewById(R.id.submitEmailButton)

        emailEditText.doOnTextChanged {text, _, _, _ ->
            val emailIsBlank = text?.isBlank() ?: true

            //If both fields are filled in
            if(!emailIsBlank && passwordEditText.text.isNotBlank()) {
                //Change submit button color to red
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            } else {
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.vivid_tangerine))
            }
        }

        passwordEditText.doOnTextChanged {text, _, _, _ ->
            val passwordIsBlank = text?.isBlank() ?: true

            //If both fields are filled in
            if(!passwordIsBlank && emailEditText.text.isNotBlank()) {
                //Change submit button color to red
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            } else {
                submitButton.setBackgroundColor(ContextCompat.getColor(this, R.color.vivid_tangerine))
            }
        }

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(email.isNotBlank() && password.isNotBlank()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, task.result.toString(),
                            Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


    }
}