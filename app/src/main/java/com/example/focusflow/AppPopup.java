package com.example.focusflow;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.focusflow.AppPopupClasses.AppInfo;
import com.example.focusflow.AppPopupClasses.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppPopup extends AppCompatActivity {
    ListView listView;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_popup);
        listView = findViewById(R.id.listview);
        text = findViewById(R.id.totalapp);

        ImageButton btnBack = findViewById(R.id.backButton);
        NavigationUtility.setNavigation(this,btnBack,SelectionPage.class);


        try {
            getallapps();
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void getallapps() throws PackageManager.NameNotFoundException {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (PackageInfo info : packList) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //Check if valid application
                String appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIcon = getPackageManager().getApplicationIcon(info.packageName);
                appInfoList.add(new AppInfo(appName, appIcon));
            }
        }

        listView.setAdapter(new ListAdapter(this, appInfoList));
        text.setText(appInfoList.size() + " Apps are installed");
    }

}


