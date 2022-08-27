package com.example.goodcall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bn: BottomNavigationView = findViewById(R.id.bottomNavView)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null) {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        } else if (auth.currentUser!!.displayName.isNullOrBlank())  {
            val intent = Intent(this, ChooseNameActivity::class.java)
            startActivity(intent)
        } else {
            bn.setOnItemSelectedListener {

                currentFragment = when (it.itemId) {
                    R.id.addGroup -> AddGroupsFragment()
                    R.id.chat -> ChatFragment()
                    R.id.settings -> SettingsFragment()
                    else -> ChatFragment()
                }

                supportFragmentManager.beginTransaction().replace(R.id.main_container, currentFragment).commit()
                /*return*/ true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction().replace(R.id.main_container, ChatFragment()).commit()

    }
}