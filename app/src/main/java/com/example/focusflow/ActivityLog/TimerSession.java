package com.example.focusflow.ActivityLog;
public class TimerSession {
    private int id;
    private long startTime;
    private long endTime;
    private long timeSet;
    private int appsBlocked;

    public TimerSession(int id, long startTime, long endTime, long timeSet, int appsBlocked) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSet = timeSet;
        this.appsBlocked = appsBlocked;
    }

    // Getters
    public int getId() { return id; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getTimeSet() { return timeSet; }
    public int getAppsBlocked() { return appsBlocked; }
}
