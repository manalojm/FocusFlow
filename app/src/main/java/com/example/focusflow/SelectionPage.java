package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionPage extends AppCompatActivity {
    ListView listView;
    TextView text;
    private View viewApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);
        viewApps = findViewById(R.id.appbox);

        viewApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent movePage = new Intent(SelectionPage.this, AppPopup.class);
                startActivity(movePage);
                finish();
            }
        });


    }
}

