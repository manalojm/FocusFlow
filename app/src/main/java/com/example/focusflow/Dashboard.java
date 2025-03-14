package com.example.focusflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.focusflow.AppPopupClasses.AccessibilityService;
import com.example.focusflow.AppPopupClasses.ListAdapter;
import com.example.focusflow.AppPopupClasses.OverlayService;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;

public class Dashboard extends AppCompatActivity {
    private static boolean playing;
    private static Long time;
    private CountDownTimer countDownTimer;
    CacheStorage cache;

   static TextView progressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //Cache Storage Initiation
        cache = CachePreloader.getCacheStorage();
        playing = cache.getBlockState();
        time = cache.getTime();

        //Buttons
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);
        Button btnPlayStop = findViewById(R.id.playstop);
        progressText = findViewById(R.id.progress_text);

        //Navigation
        btnHomePage.setOnClickListener(view -> {Toast.makeText(Dashboard.this, "You're already on the Dashboard!", Toast.LENGTH_SHORT).show();});
        NavigationUtility.setNavigation(this,btnStatsPage,StreaksPage.class);
        NavigationUtility.setNavigation(this,btnAccPage, ProfileActivity.class);
        NavigationUtility.setNavigation(this,progressBar,SelectionPage.class);

        //Programmatic UI Setup
        setTime(time);
        if(playing){
            startCountdown(time);
        }
        updatePlayButton(btnPlayStop);

        btnPlayStop.setOnClickListener(view->{
            if(AccessibilityService.isAppListEmpty()){
                Toast.makeText(this, "No apps selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AppPopup.class);
                startActivity(intent);
                finish();
                return;
            }
            if(checkTimeSet(time)){
                Toast.makeText(this, "Please set time first", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SelectionPage.class);
                startActivity(intent);
                finish();
                return;
            }

            if(playing){ //Clicking it when it says "Stop"
                playing=false;
                Log.d("AccessibilityService", "Stopping service...");
                stopService(new Intent(Dashboard.this, OverlayService.class));
                try {
                    countDownTimer.cancel();
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
            else{ //Clicking it when it says "Play"
                playing=true;
                startCountdown(time);
            }
            cache.saveBlockState(playing);
            updatePlayButton(btnPlayStop);
        });
    }

    public static boolean isBlocking(){
        return playing;
    }

    private boolean checkTimeSet(Long time){
        return time == 0L;
    }
    private void startCountdown(long durationMs) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = millisUntilFinished; // Update remaining time
                setTime(time); // Update UI with formatted time
                cache.saveTime(time);
            }
            @Override
            public void onFinish() {
                time = 0L;
                setTime(0);
                playing = false;
                stopService(new Intent(Dashboard.this, OverlayService.class));
                updatePlayButton(findViewById(R.id.playstop));
                Toast.makeText(Dashboard.this, "Countdown finished!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    private void updatePlayButton(Button btnPlayStop) {
        if (playing) {
            btnPlayStop.setText("Stop");
            btnPlayStop.setBackgroundColor(ContextCompat.getColor(this, R.color.StopRed));
        } else {
            btnPlayStop.setText("Play");
            btnPlayStop.setBackgroundColor(ContextCompat.getColor(this, R.color.GoGreen));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSettings();
        playing = cache.getBlockState();
        time = cache.getTime();

        if (playing && time > 0) {
            startCountdown(time);
        }
        updatePlayButton(findViewById(R.id.playstop));
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

    public static void setTime(long ms){
        if (progressText == null) {
            Log.e("Dashboard", "progressText is null. Ensure Dashboard is initialized before calling setTime.");
            return;
        }else {
            Log.e("Dashboard","IDK");
        }

        Long hours = ms / 3600000;
        Long minutes = (ms % 3600000) / 60000;
        Long seconds = ((ms % 3600000) % 60000) / 1000;
        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        progressText.setText(timeFormatted);
    }
}

