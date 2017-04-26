package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * Created by Quexten on 15-Dec-16.
 */

class TimetableFetchTask extends AsyncTask<String, Boolean, Boolean> {

    private Activity activity;
    private AccountManager accountManager;
    private CoursePlan coursePlan;
    private TimetableManager timetableManager;

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
