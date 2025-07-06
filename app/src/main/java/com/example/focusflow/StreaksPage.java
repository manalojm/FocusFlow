package com.example.focusflow;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.google.firebase.firestore.CollectionReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class StreaksPage extends AppCompatActivity {
    private TextView streakCountTextView;
    private FirebaseFirestore db;
    private String userId;
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streaks);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        streakCountTextView = findViewById(R.id.streak_days);
        calendarView = findViewById(R.id.materialCalendar);

        fetchStreakCount();
        fetchStreakDates();

        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);

        btnStatsPage.setOnClickListener(view ->
                Toast.makeText(StreaksPage.this, "You're already on the Streaks Page!", Toast.LENGTH_SHORT).show()
        );
        NavigationUtility.setNavigation(this, btnAccPage, ProfileActivity.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);
    }

    private void fetchStreakCount() {
        DocumentReference docRef = db.collection("users").document(userId)
                .collection("streaks").document("currentStreak");

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("completedDates")) {
                List<String> dateStrings = (List<String>) documentSnapshot.get("completedDates");
                List<Calendar> dates = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                for (String dateStr : dateStrings) {
                    try {
                        Date date = sdf.parse(dateStr);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        dates.add(cal);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                dates.sort(Comparator.comparing(Calendar::getTime));

                int currentStreak = 1;
                int maxStreak = 1;

                for (int i = 1; i < dates.size(); i++) {
                    Calendar prev = dates.get(i - 1);
                    Calendar curr = dates.get(i);

                    Calendar prevPlusOne = (Calendar) prev.clone();
                    prevPlusOne.add(Calendar.DAY_OF_YEAR, 1);

                    if (curr.get(Calendar.YEAR) == prevPlusOne.get(Calendar.YEAR) &&
                            curr.get(Calendar.DAY_OF_YEAR) == prevPlusOne.get(Calendar.DAY_OF_YEAR)) {
                        currentStreak++;
                        maxStreak = Math.max(maxStreak, currentStreak);
                    } else {
                        currentStreak = 1;
                    }
                }

                streakCountTextView.setText(String.valueOf(currentStreak));
                checkAchievements(currentStreak);

            } else {
                streakCountTextView.setText("0");
            }
        }).addOnFailureListener(e ->
                Toast.makeText(StreaksPage.this, "Failed to load streak data", Toast.LENGTH_SHORT).show()
        );
    }


    private void fetchStreakDates() {
        db.collection("users").document(userId)
                .collection("streaks").document("currentStreak")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists() || !documentSnapshot.contains("completedDates")) {
                        Log.d("StreaksPage", "No streak data found.");
                        return;
                    }

                    HashSet<CalendarDay> streakDays = new HashSet<>();
                    for (String dateStr : (Iterable<String>) documentSnapshot.get("completedDates")) {
                        Log.d("StreaksPage", "Fetched date: " + dateStr); // Debug log

                        String[] parts = dateStr.split("-");
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]) - 1;
                        int day = Integer.parseInt(parts[2]);

                        streakDays.add(CalendarDay.from(year, month, day));
                    }

                    if (!streakDays.isEmpty()) {
                        Log.d("StreaksPage", "Total streak dates: " + streakDays.size());

                        StreakDecorator streakDecorator = new StreakDecorator(
                                StreaksPage.this, streakDays, R.drawable.streaks_badge);
                        calendarView.addDecorator(streakDecorator);
                        calendarView.invalidateDecorators();
                    } else {
                        Log.d("StreaksPage", "No streak dates found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("StreaksPage", "Failed to load streak dates", e));
    }

    private void checkAchievements(int currentStreak) {

        Log.d("Achievements", "Checking achievements for streak: " + currentStreak);

        Map<String, Integer> achievementThresholds = new HashMap<>();
        achievementThresholds.put("achievement_1_day", 1);
        achievementThresholds.put("achievement_7_days", 7);
        achievementThresholds.put("achievement_30_days", 30);
        achievementThresholds.put("achievement_100_days", 100);

        CollectionReference achievementsRef = db.collection("users").document(userId).collection("achievements");

        for (Map.Entry<String, Integer> entry : achievementThresholds.entrySet()) {
            String achievementId = entry.getKey();
            int requiredDays = entry.getValue();

            if (currentStreak >= requiredDays) {
                achievementsRef.document(achievementId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().exists()) {
                            unlockAchievement(achievementsRef, achievementId);
                        } else {
                            Log.d("Achievements", "Achievement '" + achievementId + "' already unlocked.");
                        }
                    } else {
                        Log.e("Achievements", "Failed to check achievement status for '" + achievementId + "'", task.getException());
                    }
                });
            }
        }
    }

    private void unlockAchievement(CollectionReference achievementsRef, String achievementId) {
        Map<String, Object> unlockedData = new HashMap<>();
        unlockedData.put("unlocked", true);
        unlockedData.put("unlockedTimestamp", System.currentTimeMillis());

        achievementsRef.document(achievementId).set(unlockedData)
                .addOnSuccessListener(aVoid -> {
                    String message = "Achievement Unlocked: " + getAchievementName(achievementId) + "!";
                    Toast.makeText(StreaksPage.this, message, Toast.LENGTH_LONG).show();
                    Log.d("Achievements", "Unlocked achievement: " + achievementId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Achievements", "Failed to unlock achievement: " + achievementId, e);
                    Toast.makeText(StreaksPage.this, "Failed to unlock achievement.", Toast.LENGTH_SHORT).show();
                });
    }

    private String getAchievementName(String achievementId) {
        switch (achievementId) {
            case "achievement_1_day":
                return "First Step";
            case "achievement_7_days":
                return "Consistency";
            case "achievement_30_days":
                return "Dedication";
            case "achievement_100_days":
                return "Focus Master";
            default:
                return achievementId;
        }
    }
}
