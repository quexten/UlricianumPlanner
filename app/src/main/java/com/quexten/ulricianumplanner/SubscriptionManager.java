package com.quexten.ulricianumplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

/**
 * Created by Quexten on 23-Dec-16.
 */

public class SubscriptionManager {

    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";
    private static final String INITIALLY_SUBSCRIBED_IDENTIFIER = "initiallySubscribed";

    Context context;

    public SubscriptionManager(Context context) {
        this.context = context;
    }

    public void setCourse(Course oldCourse, Course course, Day day, Hour hour) {
        unsubscribe(oldCourse, day, hour);
        subscribe(course, day, hour);
    }

    private void subscribe(Course course, Day day, Hour hour) {
        if((course != null) && !course.teacher.isEmpty()) {
            FirebaseMessaging.getInstance().subscribeToTopic(getTopicName(getShortDayName(day), getShortTimeName(hour), course.teacher, course.subject));
        }
    }

    private void unsubscribe(Course course, Day day, Hour hour) {
        if(course != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getTopicName(getShortDayName(day), getShortTimeName(hour), course.teacher, course.subject));
    }

    private String getTopicName(String day, String time, String teacher, String subject) {
        return ("substitution-updates-"+day + "-" + time + "-" + teacher + "-" + subject)
                .toLowerCase()
                .replace("ö", "o_")
                .replace("ä", "a_")
                .replace("ü", "u_")
                .replace(" ", "%");
    }

    String getShortDayName(Day day) {
        switch(day) {
            case MON:
                return "mon";
            case TUE:
                return "tue";
            case WED:
                return "wed";
            case THU:
                return "thu";
            case FRI:
                return "fri";
        }
        return "mon";
    }

    String getShortTimeName(Hour hour) {
        switch(hour) {
            case ONETWO:
                return "12";
            case THREFOUR:
                return "34";
            case FIVESIX:
                return "56";
            case EIGHTNINE:
                return "89";
            case TENELEVEN:
                return "1011";
        }
        return "12";
    }

    public void subscribeToPlan(CoursePlan plan) {
        if(!isInitiallySubscribed()) {
            Course[][] courses = plan.courseArray;
            for (int dayId = 0; dayId < courses.length; dayId++) {
                for (int hourId = 0; hourId < courses[0].length; hourId++) {
                    subscribe(courses[dayId][hourId], Day.fromInt(dayId), Hour.fromInt(hourId));
                }
            }
            setInitiallySubscribed();
        }
    }

    public void setInitiallySubscribed() {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(INITIALLY_SUBSCRIBED_IDENTIFIER, true);
        editor.commit();
    }

    public boolean isInitiallySubscribed() {
        SharedPreferences sharedPref = context.getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(INITIALLY_SUBSCRIBED_IDENTIFIER, false);
    }

}
