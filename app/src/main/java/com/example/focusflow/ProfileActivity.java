package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Find views
        TextView profileName = findViewById(R.id.profile_name);
        TextView profileUsername = findViewById(R.id.profile_username);

        // Set user details (Replace with actual data)
        profileName.setText("John Doe");
        profileUsername.setText("@johndoe");

        //Buttons
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        //Navigation
        btnAccPage.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Notice")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log out", (dialog, which) -> {
                        dialog.dismiss();
                        //add logout here
                    })
                    .setNegativeButton("Keep me Logged In", (dialog, which) -> {
                        finish();
                    })
                        .show();

        });

        NavigationUtility.setNavigation(this,btnStatsPage,StreaksPage.class);
        NavigationUtility.setNavigation(this,btnHomePage, Dashboard.class);
    }
}
