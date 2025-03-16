package com.example.focusflow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import com.example.focusflow.ActivityLog.TimerSessionAdapter;
import com.example.focusflow.ActivityLog.TimerSessionLogger;
import com.example.focusflow.ActivityLog.TimerSession;
import androidx.appcompat.app.AppCompatActivity;


import java.util.List;

public class LogsPopup extends AppCompatActivity {
    private ListView listViewSessions;
    private TimerSessionAdapter adapter;
    private TimerSessionLogger sessionLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        listViewSessions = findViewById(R.id.listViewSessions);
        sessionLogger = TimerSessionLogger.getInstance(this);
        List<TimerSession> sessions = sessionLogger.getAllSessions();

        adapter = new TimerSessionAdapter(this, sessions);
        listViewSessions.setAdapter(adapter);

        Button dashboardBtn = findViewById(R.id.btnBackToDashboard);
        NavigationUtility.setNavigation(this, dashboardBtn, Dashboard.class);
    }
}