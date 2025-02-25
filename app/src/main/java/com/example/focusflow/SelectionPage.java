package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);

        //Buttons
        View viewApps = findViewById(R.id.appbox);
        ImageButton btnHomePage = findViewById(R.id.home);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStreaksPage = findViewById(R.id.stats);

        viewApps.requestFocus();

        //Navigation
        NavigationUtility.setNavigation(this,viewApps,AppPopup.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);
        NavigationUtility.setNavigation(this, btnStreaksPage, StreaksPage.class);
        NavigationUtility.setNavigation(this, btnAccPage, ProfileActivity.class);


    }
}

