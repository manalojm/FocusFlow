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
import com.example.focusflow.AppPopupClasses.OverlayService;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    private static boolean playing;
    private static Long time;
    private TimerManager timerManager;
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
        timerManager = TimerManager.getInstance();

        //Buttons
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);
        Button btnPlayStop = findViewById(R.id.playstop);
        progressText = findViewById(R.id.progress_text);

        //Navigation
        btnHomePage.setOnClickListener(view -> {Toast.makeText(this, "You're already on the Dashboard!", Toast.LENGTH_SHORT).show();});
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
                NavigationUtility.instantNavigation(this, AppPopup.class);
                return;
            }
            if(checkTimeSet(time)){
                Toast.makeText(this, "Please set time first", Toast.LENGTH_SHORT).show();
                NavigationUtility.instantNavigation(this, SelectionPage.class);
                return;
            }
            if(playing){ //Clicking it when it says "Stop"
                playing=false;
                stopService(new Intent(Dashboard.this, OverlayService.class));
                timerManager.stopTimer();
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
        timerManager.startTimer(durationMs, new TimerManager.TimerListener() {
            @Override
            public void onTick(long millisRemaining) {
                time = millisRemaining;
                setTime(time);
                cache.saveTime(time);
            }

            @Override
            public void onFinish() {
                playing = false;
                cache.saveBlockState(false);
                updatePlayButton(findViewById(R.id.playstop));
                // Update UI when timer finishes
                markTimerCompleted();            }
        }, this);


    }

    private void markTimerCompleted() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference streakRef = db.collection("users").document(userId)
                .collection("streaks").document("currentStreak");

        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("streakCount", FieldValue.increment(1)); // Increment streak count
        data.put("completedDates", FieldValue.arrayUnion(todayDate)); // Add date to Firestore array

        streakRef.set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Streak marked successfully."))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update streak", e));
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
        playing = cache.getBlockState();
        time = cache.getTime();

        if (playing && time > 0) {
            startCountdown(time);
        }
        updatePlayButton(findViewById(R.id.playstop));
    }

    public static void setTime(long ms){

        Long hours = ms / 3600000;
        Long minutes = (ms % 3600000) / 60000;
        Long seconds = ((ms % 3600000) % 60000) / 1000;
        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        progressText.setText(timeFormatted);
    }
}

