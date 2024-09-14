package com.example.finalproject;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ArticleDetailActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, dateTextView, linkTextView;
    private Button favoriteButton;
    private NewsItem newsItem;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        titleTextView = findViewById(R.id.articleTitle);
        descriptionTextView = findViewById(R.id.articleDescription);
        dateTextView = findViewById(R.id.articleDate);
        linkTextView = findViewById(R.id.articleLink);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Get the article data passed from MainActivity
        newsItem = (NewsItem) getIntent().getSerializableExtra("newsItem");
        myDb = new DatabaseHelper(this);

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getDescription());

            // Convert the date from the newsItem to EST and 12-hour format
            String formattedDate = formatDateToEST(newsItem.getDate());
            dateTextView.setText(formattedDate);  // Set formatted date

            linkTextView.setText(newsItem.getLink());  // Set the link in the TextView

            // Set the link to be clickable and open in a browser
            linkTextView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
                startActivity(browserIntent);
            });
        }

        // Set up Favorite button click listener
        favoriteButton.setOnClickListener(v -> {
            boolean isFavorite = myDb.insertData(
                    newsItem.getTitle(),
                    newsItem.getDescription(),
                    newsItem.getDate(),
                    newsItem.getLink()
            );
            if (isFavorite) {
                Toast.makeText(ArticleDetailActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ArticleDetailActivity.this, "Already in Favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load the selected theme
    private void loadTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_FinalProject_Dark); // Use dark theme
        } else {
            setTheme(R.style.Theme_FinalProject); // Use light theme
        }
    }

    // Format the date to EST and 12-hour format
    private String formatDateToEST(String dateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        originalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat targetFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
        targetFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
