package com.example.focusflow.ActivityLog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.focusflow.Cache.CachePreloader;
import com.example.focusflow.Cache.CacheStorage;
import com.example.focusflow.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimerSessionAdapter extends BaseAdapter {
    private Context context;
    private List<TimerSession> sessionList;

    public TimerSessionAdapter(Context context, List<TimerSession> sessionList) {
        this.context = context;
        this.sessionList = sessionList;
    }

    @Override
    public int getCount() {
        return sessionList.size();
    }

    @Override
    public Object getItem(int position) {
        return sessionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return sessionList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_list_item, parent, false);
        }
        TimerSession session = sessionList.get(position);
        TextView tvSessionInfo = convertView.findViewById(R.id.tvSessionInfo);

        // Format the start and end times
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String start = sdf.format(new Date(session.getStartTime()));
        String end = session.getEndTime() != 0 ? sdf.format(new Date(session.getEndTime())) : "Ongoing";

        // Convert the time set (stored in ms) to hours and minutes.
        long timeSetMs = session.getTimeSet();
        long hours = timeSetMs / 3600000;
        long minutes = (timeSetMs % 3600000) / 60000;
        String timeSetFormatted = hours + " hours and " + minutes + " minutes";

        // For apps blocked, simply take the count from the CacheStorage blocked list.
        CacheStorage cache = CachePreloader.getCacheStorage();
        int appsBlocked = cache.getBlockedApps().size();

        String sessionInfo = "Time Set: " + timeSetFormatted + "\n" +
                "Apps Blocked: " + appsBlocked + "\n" +
                "Start: " + start + "\n" +
                "End: " + end;
        tvSessionInfo.setText(sessionInfo);
        return convertView;
    }

}
