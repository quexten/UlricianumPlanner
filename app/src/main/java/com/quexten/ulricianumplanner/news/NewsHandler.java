package com.quexten.ulricianumplanner.news;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Quexten on 24-May-17.
 */

public class NewsHandler {

    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";
    private static final String NEWS_IDENTIFIER = "com.quexten.ulricianumplanner.News";

    private Context context;

    public NewsHandler(Context context) {
        this.context = context;
    }

    public void save(News news) {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Set<String> newsStringSet = new HashSet<String>(Arrays.asList(news.asStringArray()));
        editor.putStringSet(NEWS_IDENTIFIER, newsStringSet);
        editor.commit();
    }

    public News load() {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);

        Set<String> newsStringSet = sharedPref.getStringSet(NEWS_IDENTIFIER, new HashSet<String>());
        return new News(newsStringSet.toArray(new String[newsStringSet.size()]));
    }

}
