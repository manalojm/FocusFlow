package com.example.focusflow.AppPopupClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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

        ImageView iconView = convertView.findViewById(R.id.icon);
        TextView nameView = convertView.findViewById(R.id.appName);

        AppInfo app = appList.get(position);
        iconView.setImageDrawable(app.getIcon());
        nameView.setText(app.getName());

        convertView.setOnClickListener(v -> {

        });
        return convertView;
    }


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
