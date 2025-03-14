package com.example.focusflow.AppPopupClasses;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.focusflow.Dashboard;
import com.example.focusflow.NavigationUtility;
import com.example.focusflow.R;

import java.util.Random;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayView = LayoutInflater.from(this).inflate(R.layout.blocking_overlay, null);

        // Get a random block message
        String message = getRandomBlockMessage();

        // Find the TextView and set the message
        TextView messageTextView = overlayView.findViewById(R.id.blocking_message);
        messageTextView.setText(message);

        // Set up fullscreen window parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        // Hide navigation bar (for devices with soft keys)
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        windowManager.addView(overlayView, params);

        // Dismiss button logic
        Button dismissButton = overlayView.findViewById(R.id.dismiss);
        dismissButton.setOnClickListener(v -> {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
            stopSelf();
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getRandomBlockMessage() {
        String[] messages = {
                "Stay focused! Don't let distractions win.",
                "You're better than this! Keep going.",
                "Discipline is choosing between what you want now and what you want most.",
                "Every moment counts—stay on track!",
                "Success is built on small daily habits."
        };
        return messages[new Random().nextInt(messages.length)];
    }
}
