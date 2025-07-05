package com.example.focusflow;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockedStats extends AppCompatActivity {

    Map<String, Integer> blockedApps = new HashMap<String, Integer>() {{
        put("Instagram", 15);
        put("TikTok", 25);
        put("YouTube", 10);
        put("Facebook", 5);
        put("Reddit", 20);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_stats);

        ConstraintLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setBackgroundResource(R.drawable.background); // Match streaks background

        Spinner filterSpinner = new Spinner(this);
        String[] filters = new String[]{"This Week", "This Month", "This Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
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
            label.setText(app + " - " + count);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            label.setTextColor(Color.WHITE);
            barLayout.addView(label);

            LinearLayout bar = new LinearLayout(this);
            bar.setBackgroundColor(Color.parseColor("#3F51B5"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) ((count / (float) maxValue) * 300), // Reduced width for smaller bars
                    30
            );
            params.setMargins(0, 8, 0, 0);
            bar.setLayoutParams(params);
            barLayout.addView(bar);

            row.addView(barLayout);
            chartContainer.addView(row);
        }

        // Navigation buttons (restored to streaks-style home row)
        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStreaksPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        NavigationUtility.setNavigation(this, btnStreaksPage, StreaksPage.class);
        NavigationUtility.setNavigation(this, btnAccPage, ProfileActivity.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);

    }
}
