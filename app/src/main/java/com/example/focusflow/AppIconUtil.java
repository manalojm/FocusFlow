package com.example.focusflow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppIconUtil {
    public static Drawable getAppIcon(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
