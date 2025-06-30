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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
        String message = getRandomBlockMessage(this);

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

    // Load messages from raw resources
    private List<String> loadBlockMessagesFromRaw(Context context) {
        List<String> messages = new ArrayList<>();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.block_messages);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            for (int i = 0; i < jsonArray.length(); i++) {
                messages.add(jsonArray.getString(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // Get a random block message
    private String getRandomBlockMessage(Context context) {
        List<String> messages = loadBlockMessagesFromRaw(context);
        if (messages.isEmpty()) {
            return "Focus time!"; // fallback message
        }
        return messages.get(new Random().nextInt(messages.size()));
    }
}
