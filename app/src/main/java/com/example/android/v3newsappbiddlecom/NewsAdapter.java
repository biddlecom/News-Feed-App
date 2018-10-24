package com.example.android.v3newsappbiddlecom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it does not mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate to the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param news    A list of news objects to display in a list.
     */

    public NewsAdapter(Activity context, ArrayList<News> news) {
        //Here we initialize the ArrayAdapters internal storage for the context and the list.
        //The second argument is used when the ArrayAdapter is populating a single TextView.
        //Because this is a custom adapter for 4 text views, the adapter is not going to use
        //the second argument, so it can be any value.  Here we used 0.
        super(context, 0, news);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    is the position is the list of data that should be displayed in the list item view.
     * @param convertView is the recycled view to populate.
     * @param parent      is the parent ViewGroup that is used for inflation.
     * @return the View for the position in the AdapterView.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if the existing view is being reused, if not then inflate the view.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Get the {@Link News} object located at this position in the list.
        News currentNews = getItem(position);

        //find the TextView in the list_item.xml layout with the ID story_title_text_view
        TextView storyTitleTextView = (TextView) listItemView.findViewById(R.id.story_title_text_view);
        //get the story title and cast it to a string
        String stringStoryTitle = (currentNews.getStoryTitle());
        //display the story title of the current news article in the TextView story_title_text_view
        storyTitleTextView.setText(stringStoryTitle);

        //find the TextView in the list_item.xml layout with the ID author_name_text_view
        TextView authorNameTextView = (TextView) listItemView.findViewById(R.id.author_name_text_view);
        //get the authors name and cast it to a string
        String authorName = (currentNews.getAuthorName());
        //display the authors name of the current news article in the TextView author_text_view
        authorNameTextView.setText(authorName);

        //find the TextView in the list_item.xml layout with the ID date_text_view
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        //get the date and cast it to a string (in the list_item.xml file we limited the amount of
        //characters that will be shown to "10" since we only need the date and not the time.)
        String formattedDate = (currentNews.getDate());
        //display the date of the current news story article in the TextView date_text_view
        dateTextView.setText(formattedDate);

        //find the TextView in the list_item.xml layout with the ID in_section_text_view
        TextView inSectionTextView = (TextView) listItemView.findViewById(R.id.in_section_text_view);
        //get the in section name and cast it to a string
        String inSection = (currentNews.getInSection());
        //display the in section information of the current news article in the TextView in_section_text_view
        inSectionTextView.setText(inSection);

        //return the whole list item layout (containing 4 TextViews) so that it can be shown in the ListView
        return listItemView;
    }
}