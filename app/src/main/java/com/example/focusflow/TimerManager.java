package com.example.focusflow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.focusflow.ActivityLog.TimerSessionLogger;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;

public class TimerManager {
    CacheStorage cache;
    private static TimerManager instance;
    private CountDownTimer countDownTimer;
    private long timeRemaining;
    private boolean isRunning;

    private TimerManager() {//Singleton Design
        cache = CachePreloader.getCacheStorage();
        timeRemaining = cache.getTime();
        isRunning = cache.getBlockState();
    }

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void startTimer(long duration, TimerListener listener, Context context) {
        Log.d("TimerManager", "Starting timer with duration: " + duration);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = true;
        timeRemaining = duration;
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                cache.saveTime(timeRemaining);
                if (listener != null) {
                    listener.onTick(timeRemaining);
                }
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeRemaining = 0;
                cache.saveBlockState(false);
                cache.saveTime(0L);
                TimerSessionLogger.getInstance(context).endSession();

                if (listener != null) {
                    listener.onFinish(); // Notify Dashboard
                }
            }

        }.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            Log.d("TimerManager", "Stopping timer");
            countDownTimer.cancel();
            isRunning = false;
            cache.saveTime(timeRemaining);
            cache.saveBlockState(false);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConstants.channelID)
                .setSmallIcon(R.drawable.focusflow_logo)
                .setContentTitle("Time's Up!")
                .setContentText("Timer has finished!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public interface TimerListener {
        void onTick(long millisRemaining);
        void onFinish();
    }
}
