package com.example.focusflow.AppPopupClasses;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String appName;
    private Drawable icon;

    public AppInfo(String appName,Drawable icon){
        this.appName=appName;
        this.icon=icon;
    }
    public String getName(){
        return appName;
    }

    public Drawable getIcon(){
        return icon;
    }

}
