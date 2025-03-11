package com.example.focusflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser username;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private ImageView profilePic;
    private FirebaseFirestore fStore; // Add this
    private String Uid;

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

                            profilePic.setTag("updated!");
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

        Uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("profile pic").document(Uid);
        Map<String, Object> user = new HashMap<>();
        user.put("username", username.getDisplayName());
        user.put("pfp", profilePic.getTag());
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

            public static final String TAG = "TAG";

            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Profile Picture set for "+ Uid);
            }
        });

        // display name and email of user
        TextView profileUsername = findViewById(R.id.profile_Username);
        TextView profileEmail = findViewById(R.id.profile_Email);

        // Load user details from Firestore
        loadUserDetails(profileUsername, profileEmail);


        ImageButton btnAccPage = findViewById(R.id.account);
        ImageButton btnStatsPage = findViewById(R.id.stats);
        ImageButton btnHomePage = findViewById(R.id.home);


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
    }

    //debugging purposes :P
    private void loadUserDetails(TextView profileName, TextView profileUsername) {
        if (username == null) return;

        DocumentReference userDoc = fStore.collection("users").document(username.getUid());
        userDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch name and username
                        String name = documentSnapshot.getString("name");
                        String username = documentSnapshot.getString("username");

                        // Update TextViews
                        profileName.setText(name != null ? name : "No Name");
                        profileUsername.setText(username != null ? "@" + username : "@unknown");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show());
    }
}
