package com.example.android.v3newsappbiddlecom;

public class News {

    /**
     * {@Link News} represents articles about Film and Movies that the user will be interested in.
     * it will contain the story title, the authors name, the date it was published, the section
     * it belongs to (on The Guardian website), and a website url so the user can read the article
     * on The Guardian's website.
     */

    //Story title
    private String mStoryTitle;

    //Author name
    private String mAuthorName;

    //Date
    private String mDate;

    //In section
    private String mInSection;

    //url
    private String mUrl;

    /**
     * Create a new News object that takes in 5 arguments (5 strings).
     *
     * @param storyTitle is the title of the article.
     * @param authorName is the name of the author.
     * @param date       is the date that the article was posted on The Guardian website.
     * @param inSection  is the section of The Guardian website that the article is classified under.
     * @param url        is the website URL so the user can read the full article on The Guardian website.
     */
    public News(String storyTitle, String authorName, String date, String inSection, String url) {
        mStoryTitle = storyTitle;
        mAuthorName = authorName;
        mDate = date;
        mInSection = inSection;
        mUrl = url;
    }

    //get the title of the article.
    public String getStoryTitle() {
        return mStoryTitle;
    }

    //get the author of the article.
    public String getAuthorName() {
        return mAuthorName;
    }

    //get the date that the article was posted on The Guardian website.
    public String getDate() {
        return mDate;
    }

    //get the section of The Guardian website that the article is classified under.
    public String getInSection() {
        return mInSection;
    }

    //get the website URL so the user can read the full article on The Guardian website.
    public String getUrl() {
        return mUrl;
    }
}