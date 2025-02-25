package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StreaksPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streaks);
        //Buttons
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        //Navigation
        btnStatsPage.setOnClickListener(view -> {
            // Optional: Show a toast instead of reopening the same activity
            Toast.makeText(StreaksPage.this, "You're already on the Streaks Page!", Toast.LENGTH_SHORT).show();
        });
        NavigationUtility.setNavigation(this,btnAccPage,ProfileActivity.class);
        NavigationUtility.setNavigation(this,btnHomePage, Dashboard.class);
    }
}
