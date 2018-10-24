package com.example.android.v3newsappbiddlecom;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();

    //URL from The Guardian website so the app can retrieve the news data
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?=film&movie";

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     * Which we are not, but it's good practice for the future.
     */
    private static final int NEWS_LOADER_ID = 1;

    //Adapter for the list of news stories
    private NewsAdapter mAdapter;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTexTView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);
        //Find the Empty State TextView (activity_main.xml, ID: no_data_to_display_text_view)
        //and then set the setEmptyView on it
        mEmptyStateTexTView = (TextView) findViewById(R.id.no_data_to_display_text_view);
        newsListView.setEmptyView(mEmptyStateTexTView);

        // Create a new adapter that takes an empty list of news stories as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        /**
         * set an onItemClickListener on the ListView, which sends an intent to the devices web browser
         * to open a web page so the user can read the selected article on The Guardian's website.
         */
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //find the news story that was clicked on
                News currentNews = mAdapter.getItem(position);

                //convert the string url to a URI object (to pass into the intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                //create a new intent to view the news story URI
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                //send the intent to launch a new activity (open a web browser)
                startActivity(webIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo newsNetworkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch the data
        if (newsNetworkInfo != null && newsNetworkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above (NEWS_LOADER_ID) and
            // pass in null for the bundle. Pass in this activity for the LoaderCallbacks parameter
            // (which is valid because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View mloadingCircle = findViewById(R.id.progress_circle);
            mloadingCircle.setVisibility(View.GONE);

            // Update Empty State with a "no internet connection" error message
            mEmptyStateTexTView.setText(R.string.no_internet);
        }
    }

    @Override
    //onCreateLoader instantiates and returns a new loader for the given ID
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        //This is a fun message that will appear while the data is loading.  It will appear above
        //the progress circle.
        mEmptyStateTexTView.setText(R.string.hold_on);

        Log.v("onCreateLoader = ", "On Create Loader");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        //getString retrieves a String value for the preferences (key).  The second parameter is the default
        //value for the preference (default)
        String numberOfStories = sharedPrefs.getString(getString(R.string.settings_number_of_stories_key),
                getString(R.string.settings_number_of_stories_default));

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        String fromDate = sharedPrefs.getString(getString(R.string.settings_sort_by_from_date_key),
                getString(R.string.settings_sort_by_from_date_default));

        String toDate = sharedPrefs.getString(getString(R.string.settings_sort_by_to_date_key),
                getString(R.string.settings_sort_by_to_date_default));

        //My API key for the Guardian website
        String apiKey = BuildConfig.ApiKey;

        //parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        //buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //append query parameter and its value.  For example, the "format=json"
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("order-date", "published");
        uriBuilder.appendQueryParameter("type", "article");
        uriBuilder.appendQueryParameter("page-size", numberOfStories);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("tag", "film/film");
        uriBuilder.appendQueryParameter("from-date", fromDate);
        uriBuilder.appendQueryParameter("to-date", toDate);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", apiKey);

        // return the completed URI. For example: https://content.guardianapis.com/search?=film&movie
        // &format=json&order-date=published&type=article&page-size=50&show-tags=contributor&tag=film/film
        // &from-date=2018-01-30&to-date=2018-05-30&order-by=newest&api-key=apiKey
        return new NewsLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View mLoadingCircle = findViewById(R.id.progress_circle);
        mLoadingCircle.setVisibility(View.GONE);

        //Set empty state text to display: Sorry, there is no information to display.  =(
        mEmptyStateTexTView.setText(R.string.no_data);

        Log.v("onLoadFinished = ", "On Load Finished");

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        //If there is a valid list of {@Link News} stories then add them to the adapter's
        //data set.  This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v("onLoaderReset = ", "On Loader Reset");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    //this method initializes the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the options menu we specified in the main.xml file
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int newsId = item.getItemId();
        if (newsId == R.id.action_settings) {
            Intent newsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(newsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}