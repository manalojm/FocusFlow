package com.example.focusflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // Import ImageButton
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AchievementsAdapter adapter;
    private List<Achievement> achievementList;
    private FirebaseFirestore db;
    private String userId;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.achievements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        achievementList = new ArrayList<>();
        adapter = new AchievementsAdapter(this, achievementList);
        recyclerView.setAdapter(adapter);

        backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AchievementsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


        // Fetch and display achievements
        fetchAchievements();
    }

    private void fetchAchievements() {
        List<Achievement> allAchievements = new ArrayList<>();
        allAchievements.add(new Achievement("achievement_1_day", "First Step", "Start a streak for the first time.", "url_for_1_day_badge", 1));
        allAchievements.add(new Achievement("achievement_7_days", "Consistency", "Maintain a blocking streak for 7 consecutive days.", "url_for_7_day_badge", 7));
        allAchievements.add(new Achievement("achievement_30_days", "Dedication", "Maintain a blocking streak for 30 consecutive days.", "url_for_30_day_badge", 30));
        allAchievements.add(new Achievement("achievement_100_days", "Focus Master", "Maintain a blocking streak for 100 consecutive days.", "url_for_100_day_badge", 100));

        db.collection("users").document(userId).collection("achievements")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    HashSet<String> unlockedAchievementIds = new HashSet<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        unlockedAchievementIds.add(document.getId());
                    }

                    achievementList.clear();
                    for (Achievement achievement : allAchievements) {
                        if (unlockedAchievementIds.contains(achievement.getId())) {
                            achievement.setUnlocked(true);
                            achievementList.add(achievement);
                        }
                    }


                    /*achievementList.clear();                       // Start fresh each time
                    for (Achievement achievement : allAchievements) {
                        // Mark as unlocked if its ID is in Firestore
                        achievement.setUnlocked(unlockedAchievementIds.contains(achievement.getId()));
                        // Add every badge—locked or unlocked—to the list
                        achievementList.add(achievement);
                    }*/

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AchievementsActivity", "Error fetching achievements", e);
                });
    }
}