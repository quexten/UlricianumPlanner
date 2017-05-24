package com.quexten.ulricianumplanner.substitutions;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.substitutions.Substitutions;

import java.util.ArrayList;

/**
 * Created by Quexten on 24-May-17.
 */

public class SubstitutionHandler {

    //Constants
    private static final String SUBSTITUTIONS_IDENTIFIER = "com.quexten.ulricianumplanner.Substitutions";
    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";

    private Context context;

    public SubstitutionHandler(Context context) {
        this.context = context;
    }

    public void save(Substitutions substitutions) {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        editor.putString(SUBSTITUTIONS_IDENTIFIER, gson.toJson(substitutions));
        editor.commit();
    }

    public Substitutions load() {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        if(!sharedPref.contains(SUBSTITUTIONS_IDENTIFIER))
            return new Substitutions();

        Gson gson = new Gson();
        String jsonSubstitutions = sharedPref.getString(SUBSTITUTIONS_IDENTIFIER, "");

        return gson.fromJson(jsonSubstitutions, Substitutions.class);
    }

}
