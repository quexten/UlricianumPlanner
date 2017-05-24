package com.quexten.ulricianumplanner.sync.iserv;

import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.substitutions.Substitutions;

import java.util.Date;

/**
 * Created by Quexten on 24-May-17.
 */

public class SubstitutionPlan {

    private Substitutions substitutions;
    private News news;

    public SubstitutionPlan() {
    }

    public void setSubstitutions(Substitutions substitutions) {
        this.substitutions = substitutions;
    }

    public Substitutions getSubstitutions() {
        return substitutions;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }
}