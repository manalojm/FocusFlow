package com.example.focusflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsRedirect  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_settings);
        TextView text = findViewById(R.id.directions);
        Button btnCheckSettings = findViewById(R.id.btn_checksettings);
        btnCheckSettings.setOnClickListener(view -> {
            if(checkSettings()){
                text.setText("All required settings enabled, click button to proceed.");
            }
        });
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
        return enabledServices!=null&&enabledServices.contains(this.getPackageName()); //Witchcraft idk
    }
}
