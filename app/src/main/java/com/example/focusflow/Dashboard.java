package com.example.focusflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.focusflow.AppPopupClasses.ListAdapter;
import com.example.focusflow.AppPopupClasses.OverlayService;

public class Dashboard extends AppCompatActivity {
    private static boolean playing=false;
//    private static boolean startBlocking=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //Buttons
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);
        Button btnPlayStop = findViewById(R.id.playstop);
        btnPlayStop.setOnClickListener(view->{
            if(playing){ //Clicking it when it says "Stop"
                btnPlayStop.setText("Play");
                btnPlayStop.setBackgroundColor(ContextCompat.getColor(this, R.color.GoGreen));
                playing=false;

            }
            else{ //Clicking it when it says "Play"
                if(checkSettings()==true) {
                    Toast.makeText(this, "All required permissions are enabled!", Toast.LENGTH_SHORT).show();
                }
                else return;
                btnPlayStop.setText("Stop");
                btnPlayStop.setBackgroundColor(ContextCompat.getColor(this, R.color.StopRed));
                playing=true;
                Log.d("AccessibilityService", "Stopping service...");
                stopService(new Intent(Dashboard.this, OverlayService.class));
            }
        });
        //Navigation
        btnHomePage.setOnClickListener(view -> {Toast.makeText(Dashboard.this, "You're already on the Dashboard!", Toast.LENGTH_SHORT).show();});
        NavigationUtility.setNavigation(this,progressBar,SelectionPage.class);
        NavigationUtility.setNavigation(this,btnStatsPage,StreaksPage.class);
        NavigationUtility.setNavigation(this,btnAccPage, ProfileActivity.class);

    }

    public static boolean isBlocking(){
        return playing;
    }

    private boolean checkSettings(){
        if (!isAccessEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            this.startActivity(intent);
            Toast.makeText(this, "Enable Accessibility for FocusFlow", Toast.LENGTH_LONG).show();
            return false;
        }

        //Check and ask for Overlay permission
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivity(intent);
            Toast.makeText(this, "Enable 'Draw Over Other Apps' permission.", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }

    private boolean isAccessEnabled() {
        String enabledServices = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledServices!=null&&enabledServices.contains(this.getPackageName());
    }
}

