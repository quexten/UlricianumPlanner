package com.quexten.ulricianumplanner;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Calendar;

/**
 * Created by Quexten on 01-Sep-16.
 */

class NextRoomTask extends AsyncTask<String, Boolean, Boolean> {

    private CoursePlan coursePlan;
    private NetworkManager networkManager;
    private NotificationPoster notificationPoster;
    private TeacherManager teacherManager;

    public NextRoomTask(Context context) {
        coursePlan = new CoursePlan(context, null);
        networkManager = new NetworkManager(context);
        teacherManager = new TeacherManager(context);
        notificationPoster = new NotificationPoster(context, teacherManager);
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

        Course nextCourse = coursePlan.getCourse(Day.fromInt(currentDayIndex), getNextHour());

        Course notificationCourse = new Course("Subject", "Room", "Teacher");
        notificationCourse.setSubject(nextCourse.getSubject());
        notificationCourse.setTeacher(nextCourse.getTeacher());
        notificationCourse.setRoom(nextCourse.getRoom());
        notificationCourse.setSubjectB(nextCourse.getSubjectB());
        notificationCourse.setTeacherB(nextCourse.getTeacherB());
        notificationCourse.setRoomB(nextCourse.getRoomB());

        if(notificationCourse.getTeacher().isEmpty())
            return true;

        TableEntry[] currentSubstitutions = new TableEntry[0];
        if(todayDay.ordinal() == currentDayIndex)
            currentSubstitutions = todaySubstitutions;
        if(tomorrowDay.ordinal() == currentDayIndex)
            currentSubstitutions = tomorrowSubstitutions;

        for(TableEntry entry : currentSubstitutions) {
            if(Hour.fromString(entry.getTime()).equals(getNextHour())) {
                if(entry.getType().equals("Entfall"))
                    return true;
                notificationCourse.setSubject(entry.getSubstituteSubject());
                notificationCourse.setTeacher(entry.getSubstituteTeacher());
                notificationCourse.setRoom(entry.getRoom());
                notificationCourse.setSubjectB(entry.getSubstituteSubject());
                notificationCourse.setTeacherB(entry.getSubstituteTeacher());
                notificationCourse.setRoomB(entry.getRoom());
            }
        }

        notificationPoster.postNextRoomNotification(notificationCourse);
        return true;
    }

    static Hour getNextHour() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

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
