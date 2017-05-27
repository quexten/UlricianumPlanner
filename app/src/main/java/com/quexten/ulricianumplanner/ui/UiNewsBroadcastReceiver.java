package com.quexten.ulricianumplanner.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quexten.ulricianumplanner.R;
import com.quexten.ulricianumplanner.news.News;

/**
 * Created by Quexten on 15-May-17.
 */

public class UiNewsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String[] news = intent.getStringArrayExtra("news");

        final Activity activity = ((Activity) context);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity mainActivity = ((MainActivity) activity);
                mainActivity.setNews(new News(news));
            }
        });
    }

}