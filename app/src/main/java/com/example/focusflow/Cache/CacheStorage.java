package com.example.focusflow.Cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CacheStorage {
    private static final String PREF_NAME = "FocusFlowPrefs";
    private static final String KEY_PLAYING = "playing_state";
    private static final String KEY_BLOCKED_APPS = "blocked_apps";
    private static final String KEY_TIME = "time";
    private SharedPreferences preferences;
    private static CacheStorage instance;
    private static List<String> blockedApps = new ArrayList<>(); // 🔹 Make it static

    private CacheStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadBlockedApps(); // 🔹 Load list once
    }

    public static synchronized CacheStorage getInstance(Context context) {
        if (instance == null) {
            instance = new CacheStorage(context.getApplicationContext());
        }
        return instance;
    }

    public void saveBlockState(boolean isPlaying) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PLAYING, isPlaying);
        editor.apply();
    }

    public void incrementBlockCount(String packageName) {
        int currentCount = preferences.getInt(packageName + "_block_count", 0);
        preferences.edit()
                .putInt(packageName + "_block_count", currentCount + 1)
                .apply();
    }

    public int getBlockCount(String packageName) {
        return preferences.getInt(packageName + "_block_count", 0);
    }

    public boolean getBlockState() {
        return preferences.getBoolean(KEY_PLAYING, false);
    }

    private void loadBlockedApps() {
        Set<String> appSet = preferences.getStringSet(KEY_BLOCKED_APPS, new HashSet<>());
        blockedApps.addAll(appSet); // 🔹 Add all elements to the static list
    }

    public List<String> getBlockedApps() {
        return new ArrayList<>(blockedApps);
    }
    public boolean isAppBlocked(String packageName) {
        return blockedApps.contains(packageName);
    }

    public void addBlockedApp(String app) {
        if (!blockedApps.contains(app)) {
            blockedApps.add(app);
            saveBlockedApps();
        }
    }

    public void removeBlockedApp(String app) {
        if (blockedApps.contains(app)) {
            blockedApps.remove(app);
            saveBlockedApps();
        }
    }

    private void saveBlockedApps() {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> appSet = new HashSet<>(blockedApps);
        editor.putStringSet(KEY_BLOCKED_APPS, appSet);
        editor.apply();
    }
    public void saveTime(long time) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(KEY_TIME, time);
        editor.apply();
    }

    public Map<String, Integer> getAllBlockCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (String pkg : blockedApps) {
            int count = getBlockCount(pkg);
            if (count > 0) {
                counts.put(pkg, count);
            }
        }
        return counts;
    }

    public Long getTime(){
        return preferences.getLong(KEY_TIME,0L);
    }

}
