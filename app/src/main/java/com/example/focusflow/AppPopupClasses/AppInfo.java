package com.example.focusflow.AppPopupClasses;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class AppInfo {
    PackageInfo packageInfo;

    public AppInfo(PackageInfo packageInfo){
        this.packageInfo=packageInfo;
    }

    public PackageInfo getPackageInfo(){
        return packageInfo;
    }

}
