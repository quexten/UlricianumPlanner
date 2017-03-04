package com.quexten.ulricianumplanner;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Calendar;

/**
 * Created by Quexten on 01-Sep-16.
 */

class SyncTask extends AsyncTask<String, Boolean, Boolean> {

    Context context;

    AccountManager accountManager;
    CoursePlan coursePlan;
    NetworkManager networkManager;
    NotificationPoster notificationPoster;
    Substitutions substitutions;
    TeacherManager teacherManager;

    SynchronizationListener onCompletionListener;

    public SyncTask(Context context, NewsListener newsListener) {
        this.context = context;

        accountManager = new AccountManager(context);
        coursePlan = new CoursePlan(context, null);
        networkManager = new NetworkManager(context);
        networkManager.setNewsListener(newsListener);
        teacherManager = new TeacherManager(context);
        notificationPoster = new NotificationPoster(context, teacherManager);
        substitutions = new Substitutions(context);
    }

    public SyncTask(Context context, NewsListener newsListener, SynchronizationListener onCompletionListener) {
        this(context, newsListener);
        this.onCompletionListener = onCompletionListener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if(!networkManager.isLoggedIn())
            networkManager.login(accountManager.getUsername(), accountManager.getPassword());

        coursePlan.readClassName();
        coursePlan.read();

        Substitutions substitutions = networkManager.getSubstitutions();
        //Check whether the sync was successful
        if(substitutions == null) {
            if (onCompletionListener != null)
                onCompletionListener.onSync(false);
            return true;
        }
        substitutions.setTodaySubstitutions(coursePlan.getMatching(substitutions.getTodaySubstitutions(), substitutions.getTodayDay()));
        substitutions.setTomorrowSubstitutions(coursePlan.getMatching(substitutions.getTomorrowSubstitutions(), substitutions.getTomorrowDay()));
        substitutions.saveSubstitutions();

        Day todayDay = substitutions.getTodayDay();
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

        if(currentDayIndex < todayDayIndex)
            for(int i = 0; i < todaySubstitutions.length; i++) {
                notificationPoster.postSubstitutionNotification(todaySubstitutions[i]);
            }
        if(currentDayIndex == todayDayIndex)
            for(int i = 0; i < todaySubstitutions.length; i++) {
                TableEntry entry = todaySubstitutions[i];

                Hour hour = Hour.fromString(entry.time);
                int notificationHour = getEndTimeForHour(hour);

                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if(currentHour <= notificationHour)
                    notificationPoster.postSubstitutionNotification(todaySubstitutions[i]);
            }

        for(int i = 0; i < tomorrowSubstitutions.length; i++) {
            notificationPoster.postSubstitutionNotification(tomorrowSubstitutions[i]);
        }

        if(onCompletionListener != null)
            onCompletionListener.onSync(true);
        return true;
    }

    protected void onPostExecute(Void post) {
    }

    static int getEndTimeForHour(Hour hour) {
        switch(hour) {
            case ONETWO:
                return 9;
            case THREFOUR:
                return 11;
            case FIVESIX:
                return 13;
            case EIGHTNINE:
                return 15;
            case TENELEVEN:
                return 17;
        }
        return 0;
    }

}
