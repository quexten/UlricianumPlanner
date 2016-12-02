package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by Quexten on 03-Sep-16.
 */

public class CoursePlan {

	private static final String COURSE_IDENTIFIER = "CoursePlan";
    private static final String CLASS_IDENTIFIER = "className";
    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";

	Course[][] courseArray = new Course[5][5];
	String className = "12";
    Context context;

    public CoursePlan(Context context) {
        this.context = context;
    }

    /**
     * Sets the course at a given day and hour
     * @param day - the day of the course
     * @param hour - the hour of the course
     * @param course - the course to be set
     */
	public void setCourse(Day day, Hour hour, Course course) {
		courseArray[day.ordinal()][hour.ordinal()] = course;
	}

    /**
     * Gets the course at a given time
     * @param day - the day of the course
     * @param hour - the hour of the course
     * @return - the Course at the specified time and date
     */
	public Course getCourse(Day day, Hour hour) {
		return getCourse(day.ordinal(), hour.ordinal());
	}

    /**
     * Gets the course at a given time
     * @param day - the day of the course
     * @param hour - the hour of the course
     * @return - the Course at the specified time and date
     */
    public Course getCourse(int day, int hour) {
        return courseArray[day][hour] != null ? courseArray[day][hour] : new Course("", "", "");
    }

	public void saveClassName() {
		SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(CLASS_IDENTIFIER, className);
		editor.commit();
	}

	public void readClassName() {
		SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
		className = sharedPref.getString(CLASS_IDENTIFIER, "");
	}

    /**
     * Checks whether a class name is saved
     * @return
     */
	public boolean hasClassName() {
		return context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE).contains("className");
	}

    /**
     * Saves the course plan
     */
    public void save() {
		SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		Gson gson = new Gson();
		String jsonString = gson.toJson(courseArray);
		editor.putString(COURSE_IDENTIFIER, jsonString);
		editor.commit();
	}

    /**
     * Loads the course plan
     */
    public void read() {
		SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
		Gson gson = new Gson();
		courseArray = gson.fromJson(sharedPref.getString(COURSE_IDENTIFIER, gson.toJson(new Course[5][5])), Course[][].class);
	}

    /**
     * Filters a given list of substitutions for matching ones and returns only those
     * @param entries - the unfiltered list of entries
     * @return - the matching substitution entries
     */
    public TableEntry[] getMatching(TableEntry[] entries, Day day) {
        ArrayList<TableEntry> filteredEntries = new ArrayList<TableEntry>();
        for(TableEntry entry : entries) {
            if(entry.className.contains(className)) {
                Hour hour = (entry.time.equals("3") || entry.time.equals("3 - 4") || entry.time.equals("4")) ? Hour.THREFOUR : Hour.ONETWO;
                hour = (entry.time.equals("5") || entry.time.equals("5 - 6") || entry.time.equals("6")) ? Hour.FIVESIX : hour;
                hour = (entry.time.equals("8") || entry.time.equals("8 - 9") || entry.time.equals("9")) ? Hour.EIGHTNINE : hour;
                hour = (entry.time.equals("10") || entry.time.equals("10 - 11") || entry.time.equals("10 - 12")|| entry.time.equals("11") || entry.time.equals("12")) ? Hour.TENELEVEN : hour;

                if(getCourse(day.ordinal(), hour.ordinal()).teacher.equals(entry.teacher)) {
                    filteredEntries.add(entry);
                }
            }
        }

        TableEntry[] resultArray = new TableEntry[filteredEntries.size()];
        for(int i = 0; i < filteredEntries.size(); i++)
            resultArray[i] = filteredEntries.get(i);

        return resultArray;
    }

}
