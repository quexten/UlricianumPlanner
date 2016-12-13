package com.quexten.ulricianumplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Quexten on 15-Sep-16.
 */

public class RoomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new NextRoomTask(context).execute();
    }

}
