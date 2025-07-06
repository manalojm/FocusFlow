package com.example.focusflow;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusflow.ActivityLog.TimerSessionLogger;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;

import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import java.util.List;

public class SelectionPage extends AppCompatActivity {

    EditText textHours, textMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);



        //Buttons
        LinearLayout viewApps = findViewById(R.id.appbox);
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

        loadSelectedAppIcons();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSelectedAppIcons();
    }

    private void loadSelectedAppIcons() {
        LinearLayout appbox = findViewById(R.id.appbox);
        appbox.removeAllViews();

        PackageManager pm = getPackageManager();
        CacheStorage cache = CachePreloader.getCacheStorage();
        List<String> blockedApps = cache.getBlockedApps();

        for (String packageName : blockedApps) {
            try {
                Drawable icon = pm.getApplicationIcon(packageName);

                ImageView iconView = new ImageView(this);
                iconView.setImageDrawable(icon);
                iconView.setBackgroundResource(R.drawable.rounded_background);
                iconView.setPadding(16, 16, 16, 16);
                iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                params.setMargins(10, 0, 10, 0);
                iconView.setLayoutParams(params);
                appbox.addView(iconView);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

