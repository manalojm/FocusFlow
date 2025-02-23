package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);

        //Buttons
        View viewApps = findViewById(R.id.appbox);
        viewApps.requestFocus();

        //Navigation
        NavigationUtility.setNavigation(this,viewApps,AppPopup.class);

    }
}

