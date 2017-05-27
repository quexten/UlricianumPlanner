package com.quexten.ulricianumplanner.substitutions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

/**
 * Created by Quexten on 16-May-17.
 */

public class SubstitutionsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = new Bundle();
        String jsonSubstitution = bundle.getString("substitution");
        Substitution substitution = new Gson().fromJson(jsonSubstitution, Substitution.class);
        Substitutions substitutions = new SubstitutionHandler(context).load();
        substitutions.add(substitution);
    }

}
