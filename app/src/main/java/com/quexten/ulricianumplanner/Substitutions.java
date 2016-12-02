package com.quexten.ulricianumplanner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class Substitutions {

    //Constants
    static final String TODAY_OVERRIDE_IDENTIFIER = "com.quexten.ulricianumplanner.todayoverrides";
    static final String TOMORROW_OVERRIDE_IDENTIFIER = "com.quexten.ulricianumplanner.tomorrowoverrides";
    static final String TODAY_DAY_IDENTIFIER = "com.quexten.ulricianumplanner.todayday";
    static final String TOMORROW_DAY_IDENTIFIER = "com.quexten.ulricianumplanner.tomorrowday";

    static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";

    //Content
    Context context;

    TableEntry[] todaySubstitutions;
    TableEntry[] tomorrowSubstitutions;
    Day todayDay;
    Day tomorrowDay;

    public Substitutions(Context context) {
        this.context = context;
        this.todaySubstitutions = new TableEntry[0];
        this.tomorrowSubstitutions = new TableEntry[0];
        this.todayDay = Day.MON;
        this.tomorrowDay = Day.TUE;
    }

    public void setTodayDay(Day todayDay) {
        this.todayDay = todayDay;
    }

    public Day getTodayDay() {
        return todayDay;
    }

    public void setTomorrowDay(Day tomorrowDay) {
        this.tomorrowDay = tomorrowDay;
    }

    public Day getTomorrowDay() {
        return tomorrowDay;
    }

    public void setTodaySubstitutions(TableEntry[] todaySubstitutions) {
        this.todaySubstitutions = todaySubstitutions;
    }

    public TableEntry[] getTodaySubstitutions() {
        return this.todaySubstitutions;
    }

    public void setTomorrowSubstitutions(TableEntry[] tomorrowSubstitutions) {
        this.tomorrowSubstitutions = tomorrowSubstitutions;
    }

    public TableEntry[] getTomorrowSubstitutions() {
        return this.tomorrowSubstitutions;
    }

    private void saveSubstitutions(TableEntry[] todayOverrides, TableEntry[] tomorrowOverrides, Day todayDay, Day tomorrowDay) {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        editor.putString(TODAY_DAY_IDENTIFIER, todayDay.getName());
        editor.putString(TOMORROW_DAY_IDENTIFIER, tomorrowDay.getName());
        editor.putString(TODAY_OVERRIDE_IDENTIFIER, gson.toJson(todayOverrides));
        editor.putString(TOMORROW_OVERRIDE_IDENTIFIER, gson.toJson(tomorrowOverrides));
        editor.commit();
    }

    public void saveSubstitutions() {
        saveSubstitutions(todaySubstitutions, tomorrowSubstitutions, todayDay, tomorrowDay);
    }

    public void readSubstitutions() {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        if(!sharedPref.contains(TODAY_DAY_IDENTIFIER) || (sharedPref.getString(TODAY_DAY_IDENTIFIER, null) == null))
            return;

        String todayDayString = sharedPref.getString(TODAY_DAY_IDENTIFIER, "mon");
        todayDay = todayDayString.equals("mon") ? Day.MON : todayDayString.equals("tue") ? Day.TUE : todayDayString.equals("wed") ? Day.WED : todayDayString.equals("thu") ? Day.THU : todayDayString.equals("fri") ? Day.FRI : Day.MON;

        String tomorrowDayString = sharedPref.getString(TOMORROW_DAY_IDENTIFIER, "mon");
        tomorrowDay = tomorrowDayString.equals("mon") ? Day.MON : tomorrowDayString.equals("tue") ? Day.TUE : tomorrowDayString.equals("wed") ? Day.WED : tomorrowDayString.equals("thu") ? Day.THU : tomorrowDayString.equals("fri") ? Day.FRI : Day.MON;

        todaySubstitutions = gson.fromJson(sharedPref.getString(TODAY_OVERRIDE_IDENTIFIER, "{}"), TableEntry[].class);
        tomorrowSubstitutions = gson.fromJson(sharedPref.getString(TOMORROW_OVERRIDE_IDENTIFIER, "{}"), TableEntry[].class);
    }

}
