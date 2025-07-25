package com.example.focusflow;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.focusflow.Cache.CacheStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockedStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_stats);

        CacheStorage cache = CacheStorage.getInstance(this);
        Map<String, Integer> blockedApps = cache.getAllBlockCounts();

        ConstraintLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setBackgroundResource(R.drawable.background);

        Spinner filterSpinner = new Spinner(this);
        filterSpinner.setBackgroundColor(Color.WHITE);

        String[] filters = new String[]{"This Week", "This Month", "This Year"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                filters
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        LinearLayout chartContainer = findViewById(R.id.chart_container);
        chartContainer.setOrientation(LinearLayout.VERTICAL);
        chartContainer.removeAllViews();
        chartContainer.setPadding(30, 30, 30, 30);
        chartContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        chartContainer.addView(filterSpinner);

        List<Map.Entry<String, Integer>> sortedApps = new ArrayList<>(blockedApps.entrySet());
        sortedApps.sort((e1, e2) -> e2.getValue() - e1.getValue());

        int maxValue = 1;
        for (Map.Entry<String, Integer> entry : sortedApps) {
            if (entry.getValue() > maxValue) maxValue = entry.getValue();
        }

        for (Map.Entry<String, Integer> entry : sortedApps) {
            String app = entry.getKey();
            int count = entry.getValue();

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 20);
            row.setGravity(Gravity.CENTER_VERTICAL);

            ImageView icon = new ImageView(this);
            Drawable drawable = AppIconUtil.getAppIcon(this, app);
            if (drawable != null) icon.setImageDrawable(drawable);
            else icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.focusflow_logo));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(80, 80);
            iconParams.setMargins(0, 0, 16, 0);
            icon.setLayoutParams(iconParams);
            row.addView(icon);

            LinearLayout barLayout = new LinearLayout(this);
            barLayout.setOrientation(LinearLayout.VERTICAL);

            TextView label = new TextView(this);
            String labelText;
            try {
                CharSequence labelName = getPackageManager().getApplicationLabel(
                        getPackageManager().getApplicationInfo(app, 0)
                );
                labelText = labelName + " - " + count;
            } catch (PackageManager.NameNotFoundException e) {
                labelText = app + " - " + count;
            }
            label.setText(labelText);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            label.setTextColor(Color.WHITE);
            label.setMaxLines(1);
            label.setEllipsize(TextUtils.TruncateAt.END);
            label.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            ));
            barLayout.addView(label);

            LinearLayout bar = new LinearLayout(this);
            bar.setBackgroundColor(Color.parseColor("#3F51B5"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) ((count / (float) maxValue) * 300),
                    30
            );
            params.setMargins(0, 8, 0, 0);
            bar.setLayoutParams(params);
            barLayout.addView(bar);

            row.addView(barLayout);
            chartContainer.addView(row);
        }

        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStreaksPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        NavigationUtility.setNavigation(this, btnStreaksPage, StreaksPage.class);
        NavigationUtility.setNavigation(this, btnAccPage, ProfileActivity.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);

    }
}
