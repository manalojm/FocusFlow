package com.example.focusflow;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //Buttons
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        //Navigation
        btnHomePage.setOnClickListener(view -> {
            // Optional: Show a toast instead of reopening the same activity
            Toast.makeText(Dashboard.this, "You're already on the Dashboard!", Toast.LENGTH_SHORT).show();
        });
        NavigationUtility.setNavigation(this,progressBar,SelectionPage.class);
        NavigationUtility.setNavigation(this,btnStatsPage,StreaksPage.class);
        NavigationUtility.setNavigation(this,btnAccPage, ProfileActivity.class);

    }
}
