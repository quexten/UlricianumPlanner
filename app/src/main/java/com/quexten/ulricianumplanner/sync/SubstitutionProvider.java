package com.quexten.ulricianumplanner.sync;

import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.substitutions.Substitutions;

/**
 * Created by Quexten on 15-May-17.
 * A substitution Provider prodives a list of substitutions
 * that are requested by the client.
 */

public interface SubstitutionProvider {

    public boolean sync();

    public Substitutions getSubstitutions();

    public News getNews();

}
