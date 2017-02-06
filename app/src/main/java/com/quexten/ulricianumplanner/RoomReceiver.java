package com.quexten.ulricianumplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Quexten on 15-Sep-16.
 */

public class RoomReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Prevent notifications from appearing on the weekend
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
            return;

        new NextRoomTask(context).execute();
    }

}
