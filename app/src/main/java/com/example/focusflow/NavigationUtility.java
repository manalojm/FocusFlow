package com.example.focusflow;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class NavigationUtility {
    public static void setNavigation(Activity activity, View view, final Class<?> targetActivity) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, targetActivity);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
}
