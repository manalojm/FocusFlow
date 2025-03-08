package com.example.focusflow.AppPopupClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.focusflow.R;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<AppInfo> appList;

    public ListAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }
        Switch appSwitch = convertView.findViewById(R.id.switch1);
        ImageView iconView = convertView.findViewById(R.id.icon);
        TextView nameView = convertView.findViewById(R.id.appName);

        AppInfo app = appList.get(position);
        iconView.setImageDrawable(app.getIcon());
        nameView.setText(app.getName());
        convertView.setOnClickListener(v -> {

            boolean newState = !appSwitch.isChecked();
            appSwitch.setChecked(newState);
            int newColor;
            if (newState) {
                appSwitch.getTrackDrawable().setTint(ContextCompat.getColor(context, R.color.custom_track_color));
            } else {
                newColor = (ContextCompat.getColor(context, R.color.custom_track_color));
                appSwitch.getTrackDrawable().setTint(newColor);
            }
        });
        return convertView;
    }

//    private int blendColors(int baseColor, int overlayColor, float ratio) {
//        int r = (int) ((android.graphics.Color.red(overlayColor) * ratio) + (android.graphics.Color.red(baseColor) * (1 - ratio)));
//        int g = (int) ((android.graphics.Color.green(overlayColor) * ratio) + (android.graphics.Color.green(baseColor) * (1 - ratio)));
//        int b = (int) ((android.graphics.Color.blue(overlayColor) * ratio) + (android.graphics.Color.blue(baseColor) * (1 - ratio)));
//        return android.graphics.Color.rgb(r, g, b);
//    }



    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

