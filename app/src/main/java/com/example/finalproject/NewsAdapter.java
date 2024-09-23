package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter to display a list of news articles with the option to filter them.
 * It also shows a heart icon next to each article to indicate whether it's marked as a favorite.
 */
public class NewsAdapter extends ArrayAdapter<NewsItem> implements Filterable {

    private final Context context;
    private final List<NewsItem> originalNewsList;  // The original list of all articles
    private final List<NewsItem> filteredNewsList;  // The list of articles that match the filter
    private final DatabaseHelper myDb;  // Used to check if an article is a favorite

    /**
     * Constructor to initialize the adapter with the context, list of news articles, and database helper.
     *
     * @param context   The activity or fragment using this adapter.
     * @param newsList  The list of news articles to display.
     * @param myDb      The DatabaseHelper to check if an article is a favorite.
     */
    public NewsAdapter(@NonNull Context context, List<NewsItem> newsList, DatabaseHelper myDb) {
        super(context, 0, newsList);
        this.context = context;
        this.originalNewsList = new ArrayList<>(newsList);  // Store a copy of the original list
        this.filteredNewsList = new ArrayList<>(newsList);  // Start with the full list for filtering
        this.myDb = myDb;  // Store the database helper instance
    }

    /**
     * Returns the view for each article in the list.
     * Displays the article title and a heart icon that shows whether the article is a favorite.
     *
     * @param position    The position of the article in the list.
     * @param convertView The old view to reuse (if possible), or create a new one if it's null.
     * @param parent      The parent view that this view will be attached to (ListView).
     * @return The view for the current article.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NewsItem newsItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item_layout, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        ImageView favoriteIcon = convertView.findViewById(R.id.favoriteIcon);

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());

            // Check if the article is a favorite and update the heart icon accordingly
            if (myDb.isFavorite(newsItem.getTitle())) {
                favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_filled));  // Filled heart icon
            } else {
                favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_outline));  // Outline heart icon
            }
        }

        return convertView;
    }

    /**
     * Returns the number of filtered articles.
     *
     * @return The number of filtered articles in the list.
     */
    @Override
    public int getCount() {
        return filteredNewsList.size();
    }

    /**
     * Returns the article at the given position in the filtered list.
     *
     * @param position The position of the article.
     * @return The NewsItem at the specified position.
     */
    @Override
    public NewsItem getItem(int position) {
        return filteredNewsList.get(position);
    }

    /**
     * Returns a filter that allows users to search through the news articles by title.
     *
     * @return A Filter object that filters the list based on user input.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            /**
             * Performs the filtering based on the user's input.
             *
             * @param constraint The user's search input.
             * @return FilterResults containing the filtered list of articles.
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<NewsItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalNewsList);  // If no filter, show the full list
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (NewsItem item : originalNewsList) {
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);  // Add matching articles to the filtered list
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            /**
             * Updates the adapter with the filtered list and refreshes the ListView.
             *
             * @param constraint The user's search input.
             * @param results    The filtered results.
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNewsList.clear();
                filteredNewsList.addAll((List<NewsItem>) results.values);  // Update the list with filtered articles
                notifyDataSetChanged();  // Notify the adapter to refresh the list
            }
        };
    }
}
