package com.example.finalproject;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private Button refreshButton;
    private EditText searchEditText;
    private ArrayAdapter<String> adapter;
    private ArrayList<NewsItem> newsList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme before setting the content view
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
        searchEditText = findViewById(R.id.searchEditText);


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("NewsAppPrefs", MODE_PRIVATE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // Fetch news initially
        new FetchRSSFeedTask().execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");

        // Set up ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, ArticleDetailActivity.class);
            intent.putExtra("newsItem", newsList.get(position));

            // Save last viewed article using SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lastViewedTitle", newsList.get(position).getTitle());
            editor.apply();

            startActivity(intent);
        });

        // Set up refresh button click listener
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Refreshing news...", Toast.LENGTH_SHORT).show();
            new FetchRSSFeedTask().execute("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
        });

        // Set up search filter
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
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

    // AsyncTask to fetch RSS Feed
    private class FetchRSSFeedTask extends AsyncTask<String, Void, ArrayList<NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(String... urls) {
            ArrayList<NewsItem> result = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                NewsItem currentItem = null;
                boolean insideItem = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                            currentItem = new NewsItem();
                        } else if (insideItem && parser.getName().equalsIgnoreCase("title")) {
                            currentItem.setTitle(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("description")) {
                            currentItem.setDescription(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("pubDate")) {
                            currentItem.setDate(parser.nextText());
                        } else if (insideItem && parser.getName().equalsIgnoreCase("link")) {
                            currentItem.setLink(parser.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
                        result.add(currentItem);
                        insideItem = false;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                Log.e("RSSFeed", "Error fetching RSS feed", e);
                runOnUiThread(() -> Snackbar.make(findViewById(R.id.mainLayout), "Failed to load news articles.", Snackbar.LENGTH_LONG).show());
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> result) {
            progressBar.setVisibility(ProgressBar.GONE);
            newsList.clear();
            newsList.addAll(result);
            adapter.clear();
            for (NewsItem item : result) {
                adapter.add(item.getTitle());
            }
        }
    }

    // Menu setup with Help and Favorites
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        } else if (id == R.id.action_favorites) {
            // Open FavoritesActivity
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            // Open SettingsActivity (if you have one)
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Show Help dialog
    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.help))
                .setMessage(getString(R.string.help_message))
                .setPositiveButton(getString(R.string.ok),null)
                .show();
    }

}

