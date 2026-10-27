package com.example.qinarmy.model;

public class MenuItem {
    public final String title;
    public final int iconResId;
    public final Action action;

    public enum Action {
        CONTACTS, CALLS, SMS, FM_RADIO,
        CLOCK, CALCULATOR, NOTES,
        SETTINGS, OTHER
    }

    public MenuItem(String title, int iconResId, Action action) {
        this.title = title;
        this.iconResId = iconResId;
        this.action = action;
    }
}
