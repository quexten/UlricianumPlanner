package com.quexten.ulricianumplanner.news;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Quexten on 15-May-17.
 * News broadcast receiver that saves
 * incoming broadcasts into local cache
 */

public class NewsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String[] news = intent.getStringArrayExtra("news");
        new NewsHandler(context).save(new News(news));
    }

}
