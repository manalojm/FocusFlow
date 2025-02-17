package com.example.focusflow;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AppPopup extends AppCompatActivity {
    ListView listView;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_popup);

        // initialise layout
        listView = findViewById(R.id.listview);
        text = findViewById(R.id.totalapp);
        getallapps();
    }
    public void getallapps(){
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        List<String> appNames = new ArrayList<>();
        for (PackageInfo packInfo : packList){
            if ( (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                appNames.add(appName);
            }
        }
        listView.setAdapter(new ArrayAdapter<String>(AppPopup.this, android.R.layout.simple_list_item_1, appNames));
        text.setText(appNames.size() + " Apps are installed");
    }

}

