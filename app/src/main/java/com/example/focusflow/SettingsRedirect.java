package com.example.focusflow;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsRedirect  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recurringCheck();
        requestNotificationPermission();
        setContentView(R.layout.check_settings);

        Button btnCheckSettings = findViewById(R.id.btn_checksettings);
        btnCheckSettings.setOnClickListener(view -> {
            if (checkSettings()) {
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
            }
        });
    }

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void recurringCheck(){ //So that users who have enabled settings can skip the page
        if (!isAccessEnabled()) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            return;
        }
        Toast.makeText(this, "All required settings enabled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }
    private boolean checkSettings(){
        if (!isAccessEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            this.startActivity(intent);
            Toast.makeText(this, "Enable Accessibility for FocusFlow", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            this.startActivity(intent);
            Toast.makeText(this, "Enable 'Draw Over Other Apps' for FocusFlow.", Toast.LENGTH_LONG).show();
            return false;
        }
        else return true;
    }
    private boolean isAccessEnabled() {
        String enabledServices = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledServices!=null&&enabledServices.contains(this.getPackageName()); //Witchcraft idk
    }
    @Override
    protected void onResume(){
        super.onResume();
        recurringCheck();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission is required to receive timer alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
