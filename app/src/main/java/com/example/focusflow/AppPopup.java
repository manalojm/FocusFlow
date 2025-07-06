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
        getallapps();
    }

    public void getallapps() {
        new Thread(() -> {
            List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
            List<AppInfo> appInfoList = new ArrayList<>();

            String myPackageName = getPackageName();

            for (PackageInfo info : packList) {
                boolean isUserApp = (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                boolean isNotSelf = !info.packageName.equals(myPackageName);

                if (isUserApp && isNotSelf) {
                    appInfoList.add(new AppInfo(info));
                }
            }

            runOnUiThread(() -> {
                listView.setAdapter(new ListAdapter(this, appInfoList));
                text.setText(appInfoList.size() + " Apps are installed");
            });
        }).start();
    }


}


