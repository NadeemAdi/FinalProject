package com.example.finalproject;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Utility class to handle theme changes in the app.
 * It applies the light or dark theme based on the user's saved preferences.
 */
public class ThemeUtils {

    /**
     * Applies the theme (light or dark) to the given activity.
     * The theme is determined by the user's preferences stored in SharedPreferences.
     *
     * @param activity The activity where the theme should be applied.
     */
    public static void applyTheme(Activity activity) {
        // Get the user's theme preference from SharedPreferences
        SharedPreferences sharedPreferences = activity.getSharedPreferences("SettingsPrefs", Activity.MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);

        // Apply the selected theme
        if (isDarkTheme) {
            activity.setTheme(R.style.Theme_FinalProject_Dark);  // Use dark theme
        } else {
            activity.setTheme(R.style.Theme_FinalProject);  // Use light theme
        }
    }
}
