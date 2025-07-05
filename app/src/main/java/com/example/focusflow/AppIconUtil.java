package com.example.focusflow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppIconUtil {
    public static Drawable getAppIcon(Context context, String appName) {
        PackageManager pm = context.getPackageManager();

        try {
            // This is a naive name-to-package guess. You should ideally store the actual package names.
            String packageName = null;

            if (appName.equalsIgnoreCase("Instagram")) packageName = "com.instagram.android";
            else if (appName.equalsIgnoreCase("TikTok")) packageName = "com.zhiliaoapp.musically";
            else if (appName.equalsIgnoreCase("YouTube")) packageName = "com.google.android.youtube";
            else if (appName.equalsIgnoreCase("Facebook")) packageName = "com.facebook.katana";
            else if (appName.equalsIgnoreCase("Reddit")) packageName = "com.reddit.frontpage";

            if (packageName != null) {
                return pm.getApplicationIcon(packageName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
