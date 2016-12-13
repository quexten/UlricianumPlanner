package com.quexten.ulricianumplanner;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AccountManager accountManager;
    CoursePlan coursePlan;
    FeedbackManager feedbackManager;
    NetworkManager networkManager;
    NotificationPoster notificationPoster;
    Substitutions substitutions;
    TimetableManager timetableManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        accountManager = new AccountManager(this.getApplicationContext());
        coursePlan = new CoursePlan(this.getApplicationContext());
        feedbackManager = new FeedbackManager(this, coursePlan);
        networkManager = new NetworkManager(this.getApplicationContext());
        notificationPoster = new NotificationPoster(this.getApplicationContext());
        substitutions = new Substitutions(this.getApplicationContext());
        timetableManager = new TimetableManager(MainActivity.this, coursePlan, substitutions);

        //Class Selection Screen when no class is chosen yet
        coursePlan.readClassName();
        if(!coursePlan.hasClassName()) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(R.string.builder_title);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("5");
            arrayAdapter.add("6");
            arrayAdapter.add("7");
            arrayAdapter.add("8");
            arrayAdapter.add("9");
            arrayAdapter.add("10");
            arrayAdapter.add("11");
            arrayAdapter.add("12");

            builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    coursePlan.className = arrayAdapter.getItem(which);
                    coursePlan.saveClassName();
                }
                });

            builderSingle.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    coursePlan.className = arrayAdapter.getItem(which);
                    coursePlan.saveClassName();
                }
            });
            builderSingle.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builderSingle.show();
        }

        if(!accountManager.hasAccount()) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        substitutions.readSubstitutions();

        timetableManager.generateVisuals();

        //Sync Timers
        setDailyTask(5, 0, 0, new Intent(MainActivity.this, SyncReceiver.class));
        setDailyTask(14, 0, 1, new Intent(MainActivity.this, SyncReceiver.class));
        setDailyTask(19, 0, 2, new Intent(MainActivity.this, SyncReceiver.class));

        //Room Tasks
        setDailyTask(7, 40, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(9, 30, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(11, 25, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(13, 50, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(16, 30, 3, new Intent(MainActivity.this, RoomReceiver.class));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(settingsIntent);
                return true;
            case R.id.action_help:
                Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
                MainActivity.this.startActivity(tutorialIntent);
                return true;
            case R.id.action_feedback:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                builderSingle.setTitle(getResources().getString(R.string.feedback_title));

                View child = this.getLayoutInflater().inflate(R.layout.feedback_dialog, null);
                final LinearLayout childLayout = ((LinearLayout) child);
                final TextView feedbackDescriptionView = ((TextView) childLayout.findViewById(R.id.feedback_description));
                final Switch feedbackAddTimetableSwitch = ((Switch) childLayout.findViewById(R.id.feedback_attachtimetable));
                builderSingle.setView(child);
                builderSingle.setPositiveButton(getResources().getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        feedbackManager.submitFeedback(feedbackDescriptionView.getText().toString(), feedbackAddTimetableSwitch.isChecked());
                    }
                });
                builderSingle.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDailyTask(int hour, int minute, int id, Intent intent) {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Calendar timeOff9 = Calendar.getInstance();
        timeOff9.set(Calendar.HOUR_OF_DAY, hour);
        timeOff9.set(Calendar.MINUTE, minute);
        timeOff9.set(Calendar.SECOND, 0);
        int millisDay = 1000 * 60 * 60 * 24;
        long startMillis = timeOff9.getTimeInMillis();
        if(startMillis < System.currentTimeMillis()) {
            startMillis += millisDay;
        }
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(this, id, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startMillis, millisDay, pendingIntent);
    }

}
