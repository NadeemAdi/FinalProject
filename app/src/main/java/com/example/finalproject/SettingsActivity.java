package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * SettingsActivity allows users to change the theme (light/dark) and language (English/French) of the app.
 * Theme changes are applied immediately, and the language is switched without restarting the activity.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;

    /**
     * Called when the activity is created.
     * Sets up the theme switch, language spinner, and loads user preferences.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views and SharedPreferences
        themeSwitch = findViewById(R.id.themeSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);

        // Set switch state based on the current theme
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        themeSwitch.setChecked(isDarkTheme);

        // Set up a listener for the theme switch
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the user's theme preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_theme", isChecked);
            editor.apply();

            // Restart MainActivity to apply theme changes
            restartMainActivity();
        });

        // Set up the language spinner with language options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set the spinner's initial position based on saved language preference
        String currentLang = sharedPreferences.getString("app_language", "en");
        if (currentLang.equals("fr")) {
            languageSpinner.setSelection(1); // Set French if saved
        } else {
            languageSpinner.setSelection(0); // Default to English
        }

        // Set up a listener for the language spinner
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Switch language between English and French
                String selectedLanguage = position == 0 ? "en" : "fr";
                changeLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });
    }

    /**
     * Loads the selected theme (light or dark) from user preferences.
     * This is called before setting the content view to apply the theme.
     */
    private void loadTheme() {
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_FinalProject_Dark); // Use dark theme
        } else {
            setTheme(R.style.Theme_FinalProject); // Use light theme
        }
    }

    /**
     * Changes the app's language without restarting the activity.
     * Updates the locale and saves the user's language preference.
     *
     * @param languageCode The language code ("en" for English, "fr" for French).
     */
    private void changeLanguage(String languageCode) {
        // Update the app's locale
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().createConfigurationContext(config); // Apply the new locale

        // Save the language preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        // Show a message to the user
        Toast.makeText(this, "Language changed to " + (languageCode.equals("fr") ? "French" : "English"), Toast.LENGTH_SHORT).show();
    }

    /**
     * Restarts the MainActivity to apply theme changes.
     * This method is used only when switching themes (not for language changes).
     */
    private void restartMainActivity() {
        try {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Restart MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the SettingsActivity
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error restarting activity: " + e.getMessage());
        }
    }
}
