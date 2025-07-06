package com.example.focusflow.AppPopupClasses;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import android.util.Log;

import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;
import com.example.focusflow.Dashboard;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
    private static List<String> blockedApps = new ArrayList<>();
    private static CacheStorage cacheStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        cacheStorage = CachePreloader.getCacheStorage();
        blockedApps = cacheStorage.getBlockedApps();
        Log.d("AccessibilityService", "📥 Loaded blocked apps: " + blockedApps);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null || !Dashboard.isBlocking() ||
                (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                        event.getEventType() != AccessibilityEvent.TYPE_WINDOWS_CHANGED))
            return;

        String packageName = event.getPackageName().toString();
        if (isSystemApp(packageName)) return;

        Log.d("AccessibilityService", "Detected app: " + packageName);

        if (checkAppList(packageName)) {
            Log.d("AccessibilityService", "🚨 Blocking app: " + packageName);
            startService(new Intent(this, OverlayService.class));
        }
    }

    private boolean isSystemApp(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            return (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d("AccessibilityService", "✅ Service is running!");
    }

    @Override
    public void onInterrupt() {
        Log.d("AccessibilityService", "⚠️ Accessibility Service Interrupted");
        stopService(new Intent(this, OverlayService.class));
    }

    public static void displayAllApps(){
        for(String app : blockedApps) {
            Log.d("AccessibilityService", "All blocked apps: " + app);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AccessibilityService", "⚠️ Service is being destroyed. Cleaning up...");
        stopService(new Intent(this, OverlayService.class));
    }

    public static void addApp(String app) {
        if (!blockedApps.contains(app)) {
            blockedApps.add(app);
            cacheStorage.addBlockedApp(app);
        }
    }

    public static void removeApp(String app) {
        if (blockedApps.contains(app)) {
            blockedApps.remove(app);
            cacheStorage.removeBlockedApp(app);
        }
    }

    public static boolean checkAppList(String app) {
        return blockedApps.contains(app);
    }

    public static boolean isAppListEmpty() {
        return blockedApps.isEmpty();
    }
}
