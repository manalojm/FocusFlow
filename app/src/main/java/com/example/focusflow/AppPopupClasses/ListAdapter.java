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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        Switch appSwitch = convertView.findViewById(R.id.switch1);
        ImageView iconView = convertView.findViewById(R.id.icon);
        TextView nameView = convertView.findViewById(R.id.appName);

        AppInfo appInfo = appList.get(position);
        PackageInfo packageInfo = appInfo.getPackageInfo();

        try {
            String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
            Drawable appIcon = pm.getApplicationIcon(packageInfo.packageName);

            nameView.setText(appName);
            iconView.setImageDrawable(appIcon);
        } catch (Exception e) {
            Log.e("ListAdapter", "Error retrieving app details for " + packageInfo.packageName, e);
        }

        convertView.setOnClickListener(v -> {
            boolean newState = !appSwitch.isChecked();
            appSwitch.setChecked(newState);
            int newColor;
            if (newState) {
                appSwitch.getTrackDrawable().setTint(ContextCompat.getColor(context, R.color.custom_track_color));
            } else {
                newColor = (ContextCompat.getColor(context, R.color.custom_track_color));
                appSwitch.getTrackDrawable().setTint(newColor);
            }
            Log.d("ListAdapter", "Clicked on app: " + packageInfo.packageName);
            if(AccessibilityService.checkAppList(packageInfo.packageName)){
                AccessibilityService.removeApp(packageInfo.packageName);
                return;
            }
            else{
                AccessibilityService.addApp(packageInfo.packageName);
                return;
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return appList.size();
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
