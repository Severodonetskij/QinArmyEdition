package com.example.qinarmy.model;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public final String label;
    public final String packageName;
    public final Drawable icon;

    public AppInfo(String label, String packageName, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
    }
}
