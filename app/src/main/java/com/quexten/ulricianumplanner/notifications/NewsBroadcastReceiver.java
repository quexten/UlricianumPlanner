package com.quexten.ulricianumplanner.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.quexten.ulricianumplanner.courseplan.Hour;
import com.quexten.ulricianumplanner.courseplan.TeacherManager;
import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.substitutions.SubstitutionType;

import java.util.Calendar;

/**
 * Created by Quexten on 15-May-17.
 * News broadcast receiver that saves
 * incoming broadcasts into local cache
 */

public class NewsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    }

}
