package com.quexten.ulricianumplanner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
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
    SubscriptionManager subscriptionManager;

    public CoursePlan(Context context, SubscriptionManager subscriptionManager) {
        this.context = context;
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * Sets the course at a given day and hour
     * @param day - the day of the course
     * @param hour - the hour of the course
     * @param course - the course to be set
     */
	public void setCourse(Day day, Hour hour, Course course) {
        Course oldCourse = getCourse(day, hour);
		courseArray[day.ordinal()][hour.ordinal()] = course;

        subscriptionManager.setCourse(oldCourse, course, day, hour);
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

        //Sanitize improper coursefile
        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 5; y++) {
                Course course = courseArray[x][y];
                if(course.getTeacher().contains(" ")) {
                    course.setTeacher(course.getTeacher().substring(0, course.getTeacher().indexOf(" ")));
                }
            }
        }
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
                Hour hour = Hour.fromString(entry.time);
                Course course = getCourse(day.ordinal(), hour.ordinal());
                for(String teacher : new String[] {course.getTeacher(), course.getTeacherB()})
                    if(teacher.equals(entry.teacher))
                        filteredEntries.add(entry);
            }
        }

        TableEntry[] resultArray = new TableEntry[filteredEntries.size()];
        for(int i = 0; i < filteredEntries.size(); i++)
            resultArray[i] = filteredEntries.get(i);

        return resultArray;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(courseArray);
    }

}
