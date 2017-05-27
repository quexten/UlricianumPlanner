package com.quexten.ulricianumplanner.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.quexten.ulricianumplanner.courseplan.TeacherManager;
import com.quexten.ulricianumplanner.substitutions.Substitution;

/**
 * Created by Quexten on 27-May-17.
 */

public class SubstitutionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonSubstitution = intent.getStringExtra("substitution");
        Substitution substitution = new Gson().fromJson(jsonSubstitution, Substitution.class);
        new NotificationPoster(context, new TeacherManager(context)).post(substitution);
    }

}