package com.example.focusflow.Cache;

import android.app.Application;

public class CachePreloader extends Application {
    private static CacheStorage cacheStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize CacheStorage once for the entire app
        cacheStorage = CacheStorage.getInstance(this);
    }
    public static CacheStorage getCacheStorage() {
        return cacheStorage;
    }
}
