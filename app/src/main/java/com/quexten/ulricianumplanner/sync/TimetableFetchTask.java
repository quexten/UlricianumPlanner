package com.quexten.ulricianumplanner.sync;

import android.app.Activity;
import android.os.AsyncTask;

import com.quexten.ulricianumplanner.account.AccountManager;
import com.quexten.ulricianumplanner.courseplan.CoursePlan;
import com.quexten.ulricianumplanner.ui.TimetableManager;

/**
 * Created by Quexten on 15-Dec-16.
 */

public class TimetableFetchTask extends AsyncTask<String, Boolean, Boolean> {

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
