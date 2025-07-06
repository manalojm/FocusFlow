package com.example.focusflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private final Context context;
    private final List<Achievement> achievementList;

    public AchievementsAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievementList.get(position);
        holder.nameTextView.setText(achievement.getName());
        holder.descriptionTextView.setText(achievement.getDescription());
        holder.iconImageView.setImageResource(getIconRes(achievement));
    }

    private int getIconRes(Achievement a) {                      // NEW
        if (!a.isUnlocked()) {                                   // Simple flag check
            return R.drawable.default_achievement;               // Locked icon
        }


        switch (a.getId()) {                                     // Id tells which badge
            case "achievement_1_day":
                return R.drawable.achievement_1_day;             // 1‑day badge
            case "achievement_7_days":
                return R.drawable.achievement_7_days;            // 7‑day badge
            case "achievement_30_days":
                return R.drawable.achievement_30_days;           // 30‑day badge
            case "achievement_100_days":
                return R.drawable.achievement_100_days;          // 100‑day badge
            default:
                return R.drawable.default_achievement;           // Fallback safety
        }
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView iconImageView;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.achievement_name);
            descriptionTextView = itemView.findViewById(R.id.achievement_description);
            iconImageView = itemView.findViewById(R.id.achievement_icon);
        }
    }
}