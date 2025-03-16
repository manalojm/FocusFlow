package com.example.focusflow.ActivityLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimerSessionDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timer_sessions.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "timer_sessions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_TIME_SET = "time_set";
    public static final String COLUMN_APPS_BLOCKED = "apps_blocked";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_START_TIME + " INTEGER, " +
                    COLUMN_END_TIME + " INTEGER, " +
                    COLUMN_TIME_SET + " INTEGER, " +
                    COLUMN_APPS_BLOCKED + " INTEGER DEFAULT 0" +
                    ");";

    public TimerSessionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity, drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
