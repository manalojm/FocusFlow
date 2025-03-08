package com.example.focusflow.AppPopupClasses;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import android.util.Log;

import com.example.focusflow.Dashboard;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
    private String blockedApp = "com.strava";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //If event received is not what we are looking for ("TYPE_WINDOW_STATE_CHANGED" or "TYPE_WINDOWS_CHANGED"), return
        if (event == null || event.getPackageName() == null || !Dashboard.isBlocking()||
                (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                        event.getEventType() != AccessibilityEvent.TYPE_WINDOWS_CHANGED))
            return;

        String packageName = event.getPackageName().toString();
        if (isSystemApp(packageName)) return; //If system app, return

        Log.d("AccessibilityService", "Detected app: " + packageName);

        if (packageName.equals(blockedApp)) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AccessibilityService", "⚠️ Service is being destroyed. Cleaning up...");
        stopService(new Intent(this, OverlayService.class));
    }
}
