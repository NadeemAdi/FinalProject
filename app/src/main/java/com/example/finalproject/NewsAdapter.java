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

public class NewsAdapter extends ArrayAdapter<NewsItem> implements Filterable {

    private final Context context;
    private final List<NewsItem> originalNewsList;
    private final List<NewsItem> filteredNewsList;
    private final DatabaseHelper myDb;

    // Updated constructor to accept DatabaseHelper
    public NewsAdapter(@NonNull Context context, List<NewsItem> newsList, DatabaseHelper myDb) {
        super(context, 0, newsList);
        this.context = context;
        this.originalNewsList = new ArrayList<>(newsList);
        this.filteredNewsList = new ArrayList<>(newsList);
        this.myDb = myDb; // Use DatabaseHelper passed from MainActivity
    }

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

            // Check if this article is a favorite using DatabaseHelper
            if (myDb.isFavorite(newsItem.getTitle())) {
                favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_filled));
            } else {
                favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.heart_outline));
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredNewsList.size();
    }

    @Override
    public NewsItem getItem(int position) {
        return filteredNewsList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<NewsItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalNewsList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (NewsItem item : originalNewsList) {
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNewsList.clear();
                filteredNewsList.addAll((List<NewsItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
