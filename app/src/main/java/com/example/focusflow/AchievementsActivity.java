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
    private ImageButton backButton; // Declare the ImageButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Initialize Firestore and get user ID
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.achievements_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        achievementList = new ArrayList<>();
        adapter = new AchievementsAdapter(this, achievementList);
        recyclerView.setAdapter(adapter);


        // Initialize the ImageButton (backButton)
        backButton = findViewById(R.id.backButton); // Find the ImageButton by its ID
        if (backButton != null) { // Ensure the button exists in the layout
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the ProfileActivity
                    Intent intent = new Intent(AchievementsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                }
            });
        }


        // Fetch and display achievements
        fetchAchievements();
    }

    private void fetchAchievements() {
        // First, define all possible achievements in your app.
        // In a more complex app, this could be fetched from a "master_achievements" collection.
        // For now, let's define them in code.
        List<Achievement> allAchievements = new ArrayList<>();
        allAchievements.add(new Achievement("achievement_1_day", "First Step", "Start a streak for the first time.", "url_for_1_day_badge", 1));
        allAchievements.add(new Achievement("achievement_7_days", "Consistency", "Maintain a blocking streak for 7 consecutive days.", "url_for_7_day_badge", 7));
        allAchievements.add(new Achievement("achievement_30_days", "Dedication", "Maintain a blocking streak for 30 consecutive days.", "url_for_30_day_badge", 30));
        allAchievements.add(new Achievement("achievement_100_days", "Focus Master", "Maintain a blocking streak for 100 consecutive days.", "url_for_100_day_badge", 100));

        // Now, fetch the user's unlocked achievements from Firestore
        db.collection("users").document(userId).collection("achievements")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Create a set of unlocked achievement IDs for quick lookup
                    HashSet<String> unlockedAchievementIds = new HashSet<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        unlockedAchievementIds.add(document.getId());
                    }

                    // Clear the list and only add unlocked achievements
                    achievementList.clear();
                    for (Achievement achievement : allAchievements) {
                        if (unlockedAchievementIds.contains(achievement.getId())) {
                            achievement.setUnlocked(true);
                            achievementList.add(achievement);
                        }
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AchievementsActivity", "Error fetching achievements", e);
                    // Handle error, e.g., show a Toast
                });
    }
}