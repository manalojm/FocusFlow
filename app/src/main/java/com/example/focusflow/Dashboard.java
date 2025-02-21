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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    ListView listView;
    TextView text;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setClickable(true);
        progressBar.setFocusable(true);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, SelectionPage.class);
                startActivity(intent);
                finish();
            }
        });
        ImageButton btnSelectionPage = findViewById(R.id.stats);
        btnSelectionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, StreaksPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
