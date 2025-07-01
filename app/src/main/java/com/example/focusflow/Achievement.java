package com.example.focusflow;

public class Achievement {
    private String id;
    private String name;
    private String description;
    private String imageUrl; // A URL for an icon or image for the achievement
    private int requiredValue; // For achievements that require a certain count (e.g., 7 days, 30 days)
    private boolean isUnlocked; // To track if the user has unlocked it

    // Required public no-argument constructor for Firebase Firestore
    public Achievement() {
    }

    // Constructor to create an Achievement object
    public Achievement(String id, String name, String description, String imageUrl, int requiredValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.requiredValue = requiredValue;
        this.isUnlocked = false; // By default, it's not unlocked
    }

    // Getters and Setters (important for Firestore's automatic data mapping)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(int requiredValue) {
        this.requiredValue = requiredValue;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}