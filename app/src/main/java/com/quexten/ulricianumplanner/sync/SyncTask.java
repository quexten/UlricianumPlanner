package com.quexten.ulricianumplanner.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.quexten.ulricianumplanner.NewsListener;
import com.quexten.ulricianumplanner.NotificationPoster;
import com.quexten.ulricianumplanner.Substitutions;
import com.quexten.ulricianumplanner.TableEntry;
import com.quexten.ulricianumplanner.TeacherManager;
import com.quexten.ulricianumplanner.account.AccountManager;
import com.quexten.ulricianumplanner.courseplan.CoursePlan;
import com.quexten.ulricianumplanner.courseplan.Day;
import com.quexten.ulricianumplanner.courseplan.Hour;

import java.util.Calendar;

/**
 * Created by Quexten on 01-Sep-16.
 */

public class SyncTask extends AsyncTask<String, Boolean, Boolean> {

    private AccountManager accountManager;
    private CoursePlan coursePlan;
    private NetworkManager networkManager;
    private NotificationPoster notificationPoster;

    private SynchronizationListener onCompletionListener;

    public SyncTask(Context context, NewsListener newsListener) {
        accountManager = new AccountManager(context);
        coursePlan = new CoursePlan(context, null);
        networkManager = new NetworkManager(context);
        networkManager.setNewsListener(newsListener);
        TeacherManager teacherManager = new TeacherManager(context);
        notificationPoster = new NotificationPoster(context, teacherManager);
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
        int currentDayIndex = Day.fromCalendarDay(day);
        int todayDayIndex = Day.fromCalendarDay(todayDay.ordinal());

        if(currentDayIndex < todayDayIndex)
            for(int i = 0; i < todaySubstitutions.length; i++) {
                notificationPoster.postSubstitutionNotification(todaySubstitutions[i]);
            }
        if(currentDayIndex == todayDayIndex)
            for(int i = 0; i < todaySubstitutions.length; i++) {
                TableEntry entry = todaySubstitutions[i];

                Hour hour = Hour.fromString(entry.getTime());
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

    private static int getEndTimeForHour(Hour hour) {
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
            default:
                return 9;
        }
    }

}
