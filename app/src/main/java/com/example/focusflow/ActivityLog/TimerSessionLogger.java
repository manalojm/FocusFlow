package com.example.focusflow.ActivityLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TimerSessionLogger {
    private static TimerSessionLogger instance;
    private TimerSessionDbHelper dbHelper;
    private Context context;
    private Integer currentSessionId = null; // current active session

    private TimerSessionLogger(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = new TimerSessionDbHelper(this.context);
    }

    public static synchronized TimerSessionLogger getInstance(Context context) {
        if (instance == null) {
            instance = new TimerSessionLogger(context);
        }
        return instance;
    }

    // Call this when starting a timer (or when setTime is called)
    public void startSession(long timeSet) {
        // End the current session, if one exists.
        if (currentSessionId != null) {
            endSession();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long now = System.currentTimeMillis();
        values.put(TimerSessionDbHelper.COLUMN_START_TIME, now);
        values.put(TimerSessionDbHelper.COLUMN_TIME_SET, timeSet);
        values.put(TimerSessionDbHelper.COLUMN_END_TIME, 0);
        values.put(TimerSessionDbHelper.COLUMN_APPS_BLOCKED, 0);
        long id = db.insert(TimerSessionDbHelper.TABLE_NAME, null, values);
        currentSessionId = (int) id;
        db.close();
    }

    // Call this when the timer stops or finishes
    public void endSession() {
        if (currentSessionId == null) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TimerSessionDbHelper.COLUMN_END_TIME, System.currentTimeMillis());
        db.update(TimerSessionDbHelper.TABLE_NAME, values,
                TimerSessionDbHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(currentSessionId)});
        currentSessionId = null;
        db.close();
    }

    // Call this when an app is blocked during the session
    public void incrementAppsBlocked() {
        if (currentSessionId == null) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + TimerSessionDbHelper.TABLE_NAME +
                        " SET " + TimerSessionDbHelper.COLUMN_APPS_BLOCKED +
                        " = " + TimerSessionDbHelper.COLUMN_APPS_BLOCKED + " + 1 " +
                        "WHERE " + TimerSessionDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(currentSessionId)});
        db.close();
    }

    // Retrieve all sessions for display in the log
    public List<TimerSession> getAllSessions() {
        List<TimerSession> sessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TimerSessionDbHelper.TABLE_NAME,
                null, null, null, null, null,
                TimerSessionDbHelper.COLUMN_START_TIME + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TimerSessionDbHelper.COLUMN_ID));
                long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(TimerSessionDbHelper.COLUMN_START_TIME));
                long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(TimerSessionDbHelper.COLUMN_END_TIME));
                long timeSet = cursor.getLong(cursor.getColumnIndexOrThrow(TimerSessionDbHelper.COLUMN_TIME_SET));
                int appsBlocked = cursor.getInt(cursor.getColumnIndexOrThrow(TimerSessionDbHelper.COLUMN_APPS_BLOCKED));
                sessions.add(new TimerSession(id, startTime, endTime, timeSet, appsBlocked));
            }
            cursor.close();
        }
        db.close();
        return sessions;
    }
}
