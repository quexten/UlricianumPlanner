package com.quexten.ulricianumplanner.news;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Quexten on 15-May-17.
 */

public class News {

    private String[] news = new String[0];

    public News(String[] news) {
        this.news = news;
    }

    public News() {

    }

    public String[] asStringArray() {
        return this.news;
    }

}
