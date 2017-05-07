package com.quexten.ulricianumplanner.substitutions;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.quexten.ulricianumplanner.courseplan.Day;

import java.util.ArrayList;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class Substitutions {

    //Constants
    private static final String TODAY_OVERRIDE_IDENTIFIER = "com.quexten.ulricianumplanner.todayoverrides";
    private static final String TOMORROW_OVERRIDE_IDENTIFIER = "com.quexten.ulricianumplanner.tomorrowoverrides";
    private static final String TODAY_DAY_IDENTIFIER = "com.quexten.ulricianumplanner.todayday";
    private static final String TOMORROW_DAY_IDENTIFIER = "com.quexten.ulricianumplanner.tomorrowday";

    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.ui.MainActivity";

    //Content
    private Context context;

    private TableEntry[] todaySubstitutions;
    private TableEntry[] tomorrowSubstitutions;
    private Day todayDay;
    private Day tomorrowDay;

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
        if(this.todaySubstitutions != null)
            return this.todaySubstitutions;
        else
            return new TableEntry[0];
    }

    public void insertSubstitution(TableEntry entry, Day day) {
        if(day.ordinal() > tomorrowDay.ordinal() || (day.equals(Day.MON) && tomorrowDay.equals(Day.FRI))) {
            setTodayDay(getTomorrowDay());
            setTomorrowDay(day);
            setTodaySubstitutions(getTomorrowSubstitutions());
            setTomorrowSubstitutions(new TableEntry[] {});
        }

        boolean today = day.equals(todayDay);
        ArrayList<TableEntry> entries = new ArrayList<TableEntry>();
        for(TableEntry temp : (today ? getTodaySubstitutions() : getTomorrowSubstitutions())) {
            entries.add(temp);
        }
        ArrayList<TableEntry> toRemoveEntries = new ArrayList<TableEntry>();

        for(TableEntry temp : entries) {
            if(temp.getTime().equals(entry.getTime())) {
                toRemoveEntries.add(temp);
            }
        }

        for(TableEntry toRemoveEntry : toRemoveEntries)
            entries.remove(toRemoveEntry);
        toRemoveEntries.clear();

        entries.add(entry);

        if(today)
            setTodaySubstitutions(entries.toArray(new TableEntry[entries.size()]));
        else
            setTomorrowSubstitutions(entries.toArray(new TableEntry[entries.size()]));
    }

    public void setTomorrowSubstitutions(TableEntry[] tomorrowSubstitutions) {
        this.tomorrowSubstitutions = tomorrowSubstitutions;
    }

    public TableEntry[] getTomorrowSubstitutions() {
        if(this.tomorrowSubstitutions != null)
            return this.tomorrowSubstitutions;
        else
            return new TableEntry[0];
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
        todayDay = Day.fromString(todayDayString);
        String tomorrowDayString = sharedPref.getString(TOMORROW_DAY_IDENTIFIER, "mon");
        tomorrowDay = Day.fromString(tomorrowDayString);

        todaySubstitutions = gson.fromJson(sharedPref.getString(TODAY_OVERRIDE_IDENTIFIER, "{}"), TableEntry[].class);
        tomorrowSubstitutions = gson.fromJson(sharedPref.getString(TOMORROW_OVERRIDE_IDENTIFIER, "{}"), TableEntry[].class);
    }

}
