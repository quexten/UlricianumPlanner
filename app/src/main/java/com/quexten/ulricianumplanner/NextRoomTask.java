package com.quexten.ulricianumplanner;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Quexten on 01-Sep-16.
 */

class NextRoomTask extends AsyncTask<String, Boolean, Boolean> {

    Context context;

    AccountManager accountManager;
    CoursePlan coursePlan;
    NetworkManager networkManager;
    NotificationPoster notificationPoster;
    Substitutions substitutions;

    public NextRoomTask(Context context) {
        this.context = context;

        accountManager = new AccountManager(context);
        coursePlan = new CoursePlan(context);
        networkManager = new NetworkManager(context);
        notificationPoster = new NotificationPoster(context);
        substitutions = new Substitutions(context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        coursePlan.readClassName();
        coursePlan.read();

        Substitutions substitutions = networkManager.getSubstitutions();
        substitutions.setTodaySubstitutions(coursePlan.getMatching(substitutions.getTodaySubstitutions(), substitutions.getTodayDay()));
        substitutions.setTomorrowSubstitutions(coursePlan.getMatching(substitutions.getTomorrowSubstitutions(), substitutions.getTomorrowDay()));
        substitutions.saveSubstitutions();

        Day todayDay = substitutions.getTodayDay();
        Day tomorrowDay = substitutions.getTomorrowDay();
        TableEntry[] todaySubstitutions = substitutions.getTodaySubstitutions();
        TableEntry[] tomorrowSubstitutions = substitutions.getTomorrowSubstitutions();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int currentDayIndex = day == Calendar.MONDAY ? 0
                : day == Calendar.TUESDAY ? 1
                : day == Calendar.WEDNESDAY ? 2
                : day == Calendar.THURSDAY ? 3
                : day == Calendar.FRIDAY ? 4
                : day == Calendar.SATURDAY ? 5
                : day == Calendar.SUNDAY ? 6
                : 7;

        int todayDayIndex = todayDay.equals(Day.MON) ? 0
                : todayDay.equals(Day.TUE) ? 1
                : todayDay.equals(Day.WED) ? 2
                : todayDay.equals(Day.THU) ? 3
                : todayDay.equals(Day.FRI) ? 4
                : 7;

        Course nextCourse = coursePlan.getCourse(Day.fromInt(currentDayIndex), getNextHour());

        Course notificationCourse = new Course("Subject", "Room", "Teacher");
        notificationCourse.subject = nextCourse.subject;
        notificationCourse.teacher = nextCourse.teacher;
        notificationCourse.room = nextCourse.room;

        TableEntry[] currentSubstitutions = new TableEntry[0];
        if(todayDay.ordinal() == currentDayIndex)
            currentSubstitutions = todaySubstitutions;
        if(tomorrowDay.ordinal() == currentDayIndex)
            currentSubstitutions = tomorrowSubstitutions;

        for(TableEntry entry : currentSubstitutions) {
            if(Hour.fromString(entry.time).equals(getNextHour())) {
                notificationCourse.subject = entry.substituteSubject;
                notificationCourse.teacher = entry.substituteTeacher;
                notificationCourse.room = entry.room;
            }
        }

        notificationPoster.postNextRoomNotification(notificationCourse);
        return true;
    }

    static Hour getNextHour() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);

        if(hour < 8)
            return Hour.ONETWO;
        else if (hour < 10)
            return Hour.THREFOUR;
        else if (hour < 12)
            return Hour.FIVESIX;
        else if (hour < 15)
            return Hour.EIGHTNINE;
        else
            return Hour.TENELEVEN;
    }

}
