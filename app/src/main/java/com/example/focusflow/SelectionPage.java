package com.example.focusflow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusflow.ActivityLog.TimerSessionLogger;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;

public class SelectionPage extends AppCompatActivity {

    EditText textHours, textMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);

        //Buttons
        View viewApps = findViewById(R.id.appbox);
        ImageButton btnHomePage = findViewById(R.id.home);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStreaksPage = findViewById(R.id.stats);
        Button setTimeButton = findViewById(R.id.restrict_btn);
        //Text
        textHours = findViewById(R.id.edtHour);
        textMinute = findViewById(R.id.edtMinute);

        setTimeButton.setOnClickListener(v->{
            Long hours = Long.parseLong(textHours.getText().toString());
            Long minutes = Long.parseLong(textMinute.getText().toString());
            Long ms = hours*3600000+minutes*60000;
            String timeFormatted = String.format(hours+" hours and "+minutes+" minutes");
            Toast.makeText(this, "Time Set\n"+timeFormatted, Toast.LENGTH_SHORT).show();
            CacheStorage cache = CachePreloader.getCacheStorage();
            cache.saveTime(ms);
            TimerSessionLogger.getInstance(SelectionPage.this).startSession(ms);
        });

        viewApps.requestFocus();

        NavigationUtility.setNavigation(this,viewApps,AppPopup.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);
        NavigationUtility.setNavigation(this, btnStreaksPage, StreaksPage.class);
        NavigationUtility.setNavigation(this, btnAccPage, ProfileActivity.class);


    }
}

