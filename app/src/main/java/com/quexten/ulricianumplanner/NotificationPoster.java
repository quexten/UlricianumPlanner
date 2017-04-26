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

    private Context context;
    private TeacherManager teacherManager;

    //Counter to ensure unique notifications
    private static int notificationId = 0;

    public NotificationPoster(Context context, TeacherManager teacherManager) {
        this.context = context;
        this.teacherManager = teacherManager;
    }

    public void postSubstitutionNotification(TableEntry entry) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPref.getBoolean("notifications_enabled", true))
            return;

        //Vibration
        if(sharedPref.getBoolean("notifications_vibrate", true))
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[] {200, 100, 200, 100, 200, 100, 200}, -1);

        //Notification
        String message = "";
        String header = "";

        String substitutionTeacher = teacherManager.getFullTeacherName(entry.getSubstituteTeacher());
        substitutionTeacher = (substitutionTeacher != null) ? substitutionTeacher : entry.getSubstituteTeacher();
        String teacher = teacherManager.getFullTeacherName(entry.getTeacher());
        teacher = (teacher != null) ? teacher : entry.getTeacher();
        String substituteSubject = Course.getLongSubjectName(context, entry.getSubstituteSubject());
        String subject = Course.getLongSubjectName(context, entry.getSubject());
        String time = entry.getTime();
        String room = entry.getRoom();

        switch(entry.getType()) {
            case "Entfall":
                message = time + " entfällt " + subject + " bei " + teacher;
                header = time + " Entfall";
                break;
            case "Verleg.":
                message = subject + " wird verlegt.";
                header = subject + " Verlegung";
                break;
            case "Raumä.":
                message = "Nach " + room;
                header = subject + " Raumänderung";
                break;
            case "Vertret.":
                message = "Durch " + substitutionTeacher;
                header = subject + " Vertretung";
                break;
            case "Tausch":
                message = "Mit " + substituteSubject + " bei " + substitutionTeacher;
                header = subject + " Tausch";
                break;
            case "trotz A.":
                message = "in " + room;
                header = subject + " findet statt.";
                break;
            case "Betreu.":
                message = "Bei " + substitutionTeacher + " in " + entry.getRoom();
                header = subject + " Betreuung";
                break;
            default:
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
                        .setColor(Color.argb(255, 196, 0, 0))
                        .setLights(Color.argb(255, 255, 255, 0), 500, 500)
                        .addAction(R.drawable.ic_share, context.getResources().getString(R.string.share_button), getSharingIntent(context, message));

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

        String currentTeacher = course.getTeacher();
        String currentSubject = course.getSubject();
        String currentRoom = course.getRoom();

        //Notification
        String header = "Gleich " + Course.getLongSubjectName(context, currentSubject) + " in " + currentRoom;
        String teacher = teacherManager.getFullTeacherName(currentTeacher);
        teacher = (teacher != null) ? teacher : currentTeacher;
        String message = "bei " + teacher;

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

    private PendingIntent getSharingIntent(Context context, String message) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        sendIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return resultPendingIntent;
    }

}
