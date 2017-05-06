package com.quexten.ulricianumplanner.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.quexten.ulricianumplanner.NewsListener;

/**
 * Created by Quexten on 15-Sep-16.
 */

public class SyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NewsListener newsListener = new NewsListener(context) {
            @Override
            public void newsReceived(String news) {
                this.saveNews(news);
            }
        };
        new SyncTask(context, newsListener).execute();
    }

}
