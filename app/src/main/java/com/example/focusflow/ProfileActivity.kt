package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        // Find views
        val profileName = findViewById<TextView>(R.id.profile_name)
        val profileUsername = findViewById<TextView>(R.id.profile_username)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val homeButton = findViewById<ImageButton>(R.id.home)
        val accountButton = findViewById<ImageButton>(R.id.account)
        val statsButton = findViewById<ImageButton>(R.id.stats)

        // Set user details (Replace with actual data)
        profileName.text = "John Doe"
        profileUsername.text = "@johndoe"

        // Button Click Listeners
        backButton.setOnClickListener {
            finish()  // Go back to the previous screen
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        accountButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }


}