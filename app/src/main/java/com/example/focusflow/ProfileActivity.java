package com.example.focusflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser username;
    private FirebaseUser email;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView profilePic;
    private FirebaseFirestore fStore; // Add this
    private String Uid;


    private void savePFPtoFirestore(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        Uid = currentUser.getUid();
        DocumentReference documentReference = fStore.collection("users").document(Uid);

        Map<String, Object> user = new HashMap<>();
        user.put("pfp", imageUrl); // Only updating the profile picture field

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                documentReference.update(user) // Update only the "pfp" field
                        .addOnSuccessListener(unused -> Log.d("TAG", "Profile picture updated for " + Uid))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating profile picture", e));
            } else {
                documentReference.set(user) // Create document if it doesn't exist
                        .addOnSuccessListener(unused -> Log.d("TAG", "Profile created with picture for " + Uid))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error creating profile", e));
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching user document", e));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        mAuth = FirebaseAuth.getInstance();
        username = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance(); // Initialize Firestore


        if (username == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return;
        }

        profilePic = findViewById(R.id.profilePic);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            Glide.with(this).load(selectedImageUri).into(profilePic);

                            // Save to Firestore
                            savePFPtoFirestore(String.valueOf(selectedImageUri));
                        }
                    }
                });

        profilePic.setOnClickListener(view -> {
            ImagePicker.with(ProfileActivity.this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        // display name and email of user
        TextView profileUsername = findViewById(R.id.profile_Username);
        TextView profileEmail = findViewById(R.id.profile_Email);

        // Load user details from Firestore
        loadUserDetails(profileUsername, profileEmail);


        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);
        ImageButton btnLog = findViewById(R.id.ActivityLog);


        btnAccPage.setOnClickListener(view -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Notice")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log out", (dialog, which) -> {
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Keep me Logged In", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        NavigationUtility.setNavigation(this, btnStatsPage, StreaksPage.class);
        NavigationUtility.setNavigation(this, btnHomePage, Dashboard.class);
        NavigationUtility.setNavigation(this, btnLog, LogsPopup.class);

        username = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        Button btnBackup = findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    String uid = username.getUid();
                    fetchAndBackupStreaks(uid);
                }
            }
        });

        Button btnRestore = findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null) {
                    String uid = username.getUid();
                    restoreStreakBackup(uid);
                }
            }
        });

        Button btnAchievements = findViewById(R.id.btn_achievements); // Make sure you have this button in your XML
        NavigationUtility.setNavigation(this, btnAchievements, AchievementsActivity.class);
    }

    private void loadUserDetails(TextView profileUsername, TextView profileEmail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userDoc = fStore.collection("users").document(userId);

            userDoc.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String email = documentSnapshot.getString("email");
                    String profileImageUrl = documentSnapshot.getString("pfp");

                    profileUsername.setText(username != null ? username : "No Name");
                    profileEmail.setText(email != null ? email : "@unknown");

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(profilePic);
                    }
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(ProfileActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndBackupStreaks(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference streakRef = db.collection("users").document(uid)
                .collection("streaks").document("currentStreak");

        streakRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> streakData = documentSnapshot.getData();

                        if (streakData != null && !streakData.isEmpty()) {
                            backupStreakData(uid, streakData);
                        } else {
                            Toast.makeText(ProfileActivity.this, "No streak data found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "No currentStreak document", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed to fetch streaks: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void backupStreakData(String uid, Map<String, Object> streakData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> backup = new HashMap<>();
        backup.put("streak", streakData);
        backup.put("backupTimestamp", System.currentTimeMillis());

        db.collection("backups").document(uid)
                .set(backup)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileActivity.this, "Backup successful!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void restoreStreakBackup(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("backups").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> streakData = (Map<String, Object>) documentSnapshot.get("streak");

                        if (streakData != null && !streakData.isEmpty()) {
                            db.collection("users").document(uid)
                                    .collection("streaks").document("currentStreak")
                                    .set(streakData)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(ProfileActivity.this, "Streak restored!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(ProfileActivity.this, "Failed to restore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(ProfileActivity.this, "No streak data in backup", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "No backup found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed to retrieve backup: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void writeStreaksToFirestore(String uid, List<Map<String, Object>> streaks) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Map<String, Object> streak : streaks) {
            // Optional: provide your own document ID logic
            db.collection("streaks").document(uid)
                    .collection("entries")
                    .add(streak)
                    .addOnFailureListener(e ->
                            Log.e("Restore", "Failed to restore streak: " + e.getMessage()));
        }

        Toast.makeText(ProfileActivity.this, "Streaks restored from backup", Toast.LENGTH_SHORT).show();
    }

}