package com.example.android.v3newsappbiddlecom;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    //Tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Query The Guardian API data set and return a list of {@Link News} objects
    public static List<News> fetchNewsData(String requestUrl) {

        //this is here so that I can test the progress circle and to check the fun @string hold_on message.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v("fetchNewsData = ", "Fetching News Data");
        //Create a URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        //Extract relevant fields from the JSON response and create a list of {@Link News} stories
        List<News> newsStories = extractNewsFromJson(jsonResponse);

        //Return a list of {@Link News} stories
        return newsStories;
    }

    //Returns a new URL object from the given String URL
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    //Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News Stories JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Return a list of {@Link News} objects that have been built up from parsing the given JSON response
    private static List<News> extractNewsFromJson(String newsJson) {
        //If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        List<News> newsStories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJson);

            // Extract the JSONObject associated with the key called "response",
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of properties (webTitle, webPublicationDate, sectionName and
            // webUrl) for the individual news stories.
            JSONArray newsArray = newsObject.getJSONArray("results");

            // For each news story in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single news story at position i within the list of news stories
                JSONObject currentNewsStory = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String storyTitle = currentNewsStory.getString("webTitle");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNewsStory.getString("webPublicationDate");

                // Extract the value for the key called "sectionName"
                String inSection = currentNewsStory.getString("sectionName");

                //Extract the value of the key called "url"
                String url = currentNewsStory.getString("webUrl");

                // Extract the JSONArray associated with key "tags", which represents information
                // about the author of the article.
                JSONArray tags = currentNewsStory.getJSONArray("tags");

                // Extract the value for the key called "webTitle"
                String authorName = currentNewsStory.getString("webTitle");

                if (tags.length() == 0) {
                    authorName = String.valueOf("Unknown Author");
                } else {
                    for (int nameLength = 0; nameLength < tags.length(); nameLength++) {
                        // Get the webTitle (author's name) from the list of tags.
                        JSONObject currentContributor = tags.getJSONObject(nameLength);
                        // Extract the value for the key "webTitle", which represents
                        // the first and last name of the author.
                        authorName = currentContributor.getString("webTitle");
                    }
                }

                // Create a new {@link News} object with the webTitle (story title), "tags" webTitle
                //(authors first and last name), webPublicationDate (date), sectionName (in section)
                //and webUrl (url).
                News news = new News(storyTitle, authorName, date, inSection, url);

                // Add the new {@link News} to the list of news stories.
                newsStories.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message with the
            // message from the exception.
            Log.e("QueryUtils", "Problem parsing the news stories JSON results", e);
        }

        // Return the list of News Stories
        return newsStories;
    }
}