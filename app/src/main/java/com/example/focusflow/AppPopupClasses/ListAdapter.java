package com.example.focusflow.AppPopupClasses;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.focusflow.AppPopup;
import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;
import com.example.focusflow.R;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<AppInfo> appList;
    private PackageManager pm;

    public ListAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
        this.pm = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Initialize convertView if null
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        // Initialize all views first
        Switch appSwitch = convertView.findViewById(R.id.switch1);
        ImageView iconView = convertView.findViewById(R.id.icon);
        TextView nameView = convertView.findViewById(R.id.appName);
        TextView countView = convertView.findViewById(R.id.blockCount); // This is the correct line

        // Now get the app info
        AppInfo appInfo = appList.get(position);
        PackageInfo packageInfo = appInfo.getPackageInfo();

        // Set basic app info
        String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
        nameView.setText(appName);
        iconView.setImageResource(R.drawable.focusflow_logo);

        // Set block count
        CacheStorage cache = CachePreloader.getCacheStorage();
        int blockCount = cache.getBlockCount(packageInfo.packageName);
        countView.setText(String.valueOf(blockCount));

        // Thread for loading icons without freezing UI
        new Thread(() -> {
            try {
                Drawable appIcon = pm.getApplicationIcon(packageInfo.packageName);
                ((AppPopup) context).runOnUiThread(() -> iconView.setImageDrawable(appIcon));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("ListAdapter", "App icon not found for " + packageInfo.packageName, e);
            }
        }).start();

        boolean isChecked = getSwitchState(packageInfo.packageName);
        setSwitchState(appSwitch, isChecked);

        convertView.setOnClickListener(v -> {
            appSwitch.toggle();
            setSwitchState(appSwitch, appSwitch.isChecked());
            Log.d("ListAdapter", "Clicked on app: " + packageInfo.packageName);

            if (AccessibilityService.checkAppList(packageInfo.packageName)) {
                AccessibilityService.removeApp(packageInfo.packageName);
            } else {
                AccessibilityService.addApp(packageInfo.packageName);
                // Increment the block count when blocking an app
                cache.incrementBlockCount(packageInfo.packageName);
                // Update the count display
                int newCount = cache.getBlockCount(packageInfo.packageName);
                countView.setText(String.valueOf(newCount));
            }
        });

        return convertView;
    }

    private void setSwitchState(Switch appSwitch, boolean isChecked) {
        appSwitch.setChecked(isChecked);
        if (isChecked) {
            appSwitch.getThumbDrawable().setTint(ContextCompat.getColor(context, R.color.custom_track_color));
        } else {
            appSwitch.getThumbDrawable().setTint(ContextCompat.getColor(context, R.color.custom_track_color_off));
        }
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    private boolean getSwitchState(String packageName) {
        CacheStorage cache = CachePreloader.getCacheStorage();
        return cache.isAppBlocked(packageName);
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}