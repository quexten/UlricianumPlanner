package com.quexten.ulricianumplanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class NotificationPoster {

    Context context;

    static int notificationId = 0;

    public NotificationPoster(Context context) {
        this.context = context;
    }

    public void postSubstitutionNotification(TableEntry entry) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPref.getBoolean("notifications_enabled", true))
            return;

        //Vibration
        if(sharedPref.getBoolean("notifications_vibrate", true))
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);

        //Notification
        String message = "";
        String header = "";
        switch(entry.type) {
            case "Entfall":
                message = entry.time + " entfällt " + entry.subject + " bei " + entry.teacher;
                header = entry.time + " Entfall";
                break;
            case "Verleg.":
                message = entry.subject + " wird verlegt.";
                header = entry.subject + " Verlegung";
                break;
            case "Raum�.":
                message = "Raumänderung in " + entry.subject + " nach " + entry.room;
                header = entry.subject + " Raumänderung";
                break;
            case "Raumä.":
                message = "Raumänderung in " + entry.subject + " nach " + entry.room;
                header = entry.subject + " Raumänderung";
                break;
            case "Vertret.":
                message = entry.subject + " wird vertreten durch " + entry.substituteTeacher;
                header = entry.subject + " Vertretung";
                break;
            case "Tausch":
                message = entry.subject + " wird getauscht mit " + entry.substituteSubject + " bei " + entry.substituteTeacher;
                header = entry.subject + " Tausch";
                break;
            case "trotz A.":
                message = entry.subject + " findet in " + entry.room + " statt";
                header = entry.subject + " findet statt.";
                break;
            case "Betreu.":
                message = entry.subject + " wird betreut durch " + entry.substituteTeacher + " in " + entry.room;
                header = entry.subject + " Betreuung";
                break;
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_school_black_24dp :
                                R.mipmap.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.icon))
                        .setContentTitle(header)
                        .setContentText(message)
                        .setColor(Color.argb(255, 196, 0, 0));

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId++, builder.build());
    }

    public void postNextRoomNotification(Course course) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPref.getBoolean("notifications_room_enabled", true))
            return;

        //Notification
        String header = "Gleich " + course.subject + " in " + course.room;
        String message = header + " bei " + (course.getTeachers().length > 0 ? course.getTeachers()[0] : "");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_school_black_24dp :
                                R.mipmap.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.mipmap.icon))
                        .setContentTitle(header)
                        .setContentText(message);

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(-1, builder.build());
    }

}
