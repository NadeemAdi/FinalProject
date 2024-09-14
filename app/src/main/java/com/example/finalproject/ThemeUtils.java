package com.example.finalproject;

import android.app.Activity;
import android.content.SharedPreferences;

public class ThemeUtils {

    public static void applyTheme(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("SettingsPrefs", Activity.MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            activity.setTheme(R.style.Theme_FinalProject_Dark);  // Use dark theme
        } else {
            activity.setTheme(R.style.Theme_FinalProject);  // Use light theme
        }
    }
}
