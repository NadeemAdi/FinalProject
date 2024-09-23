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

public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;

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

        // Set switch state based on current theme
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        themeSwitch.setChecked(isDarkTheme);

        // Theme switch listener
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save theme preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_theme", isChecked);
            editor.apply();

            // Restart the activity to apply theme changes
            restartMainActivity();
        });

        // Set up the language spinner with available options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set the spinner position based on saved language preference
        String currentLang = sharedPreferences.getString("app_language", "en");
        if (currentLang.equals("fr")) {
            languageSpinner.setSelection(1); // Set French if saved
        } else {
            languageSpinner.setSelection(0); // Default to English
        }

        // Language spinner listener
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

    // Load the selected theme
    private void loadTheme() {
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_FinalProject_Dark); // Use dark theme
        } else {
            setTheme(R.style.Theme_FinalProject); // Use light theme
        }
    }

    // Change the language without restarting the activity
    private void changeLanguage(String languageCode) {
        // Update the app's locale using ContextWrapper
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().createConfigurationContext(config); // Update locale using ContextWrapper

        // Save language preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        // Show a message to the user
        Toast.makeText(this, "Language changed to " + (languageCode.equals("fr") ? "French" : "English"), Toast.LENGTH_SHORT).show();
    }

    // Restart the MainActivity to apply theme changes (not for language)
    private void restartMainActivity() {
        try {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Restart MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the settings activity
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error restarting activity: " + e.getMessage());
        }
    }
}
