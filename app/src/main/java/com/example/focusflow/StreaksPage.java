package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StreaksPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streaks);
        ImageButton btnSelectionPage = findViewById(R.id.stats);
        btnSelectionPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StreaksPage.this, SelectionPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
