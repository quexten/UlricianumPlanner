package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.Time;
import java.util.Calendar;

/**
 * Created by Quexten on 15-Dec-16.
 */

class TimetableFetchTask extends AsyncTask<String, Boolean, Boolean> {

    Activity activity;
    AccountManager accountManager;
    CoursePlan coursePlan;
    TimetableManager timetableManager;

    public TimetableFetchTask(Activity activity, CoursePlan coursePlan, TimetableManager timetableManager) {
        this.activity = activity;
        this.accountManager = new AccountManager(activity.getApplicationContext());
        this.coursePlan = coursePlan;
        this.timetableManager = timetableManager;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        new RemoteTimetableFetcher(activity, accountManager, coursePlan, timetableManager);
        return true;
    }

}
