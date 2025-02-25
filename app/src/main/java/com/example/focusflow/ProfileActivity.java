package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Find views
        TextView profileName = findViewById(R.id.profile_name);
        TextView profileUsername = findViewById(R.id.profile_username);
        ImageButton homeButton = findViewById(R.id.home);
        ImageButton accountButton = findViewById(R.id.account);
        ImageButton statsButton = findViewById(R.id.stats);

        // Set user details (Replace with actual data)
        profileName.setText("John Doe");
        profileUsername.setText("@johndoe");
        

        homeButton.setOnClickListener(view ->
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class))
        );

        accountButton.setOnClickListener(view ->
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class))
        );
    }
}
