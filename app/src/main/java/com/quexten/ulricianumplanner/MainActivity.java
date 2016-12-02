package com.quexten.ulricianumplanner;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AccountManager accountManager;
    CoursePlan coursePlan;
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
        networkManager = new NetworkManager(this.getApplicationContext());
        notificationPoster = new NotificationPoster(this.getApplicationContext());
        substitutions = new Substitutions(this.getApplicationContext());
        timetableManager = new TimetableManager(MainActivity.this, coursePlan, substitutions);

        //Class Selection Screen when no class is chosen yet
        coursePlan.readClassName();
        if(!coursePlan.hasClassName()) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle("Klasse Auswählen");

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

            builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    coursePlan.className = arrayAdapter.getItem(which);
                    coursePlan.saveClassName();
                }
            });
            builderSingle.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
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

        setSyncTimer(5,0);
        setSyncTimer(14,1);
        setSyncTimer(19,2);
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
            case R.id.action_sync:
                new SyncTask(this, new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                substitutions.readSubstitutions();
                                timetableManager.generateVisuals();
                                try {
                                    CharSequence text = "Synchronisiert!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(MainActivity.this.getApplicationContext(), text, duration);
                                    toast.show();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                }).execute();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(settingsIntent);
                return true;
            case R.id.action_help:
                Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
                MainActivity.this.startActivity(tutorialIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setSyncTimer(int hour, int id) {
        AlarmManager alarmMgr0 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent0 = new Intent(MainActivity.this, SyncReceiver.class);
        Calendar timeOff9 = Calendar.getInstance();
        timeOff9.set(Calendar.HOUR_OF_DAY, hour);
        timeOff9.set(Calendar.MINUTE, 0);
        timeOff9.set(Calendar.SECOND, 0);
        int millisDay = 1000 * 60 * 60 * 24;
        long startMillis = timeOff9.getTimeInMillis();
        if(startMillis < System.currentTimeMillis()) {
            startMillis += millisDay;
        }
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(this, id, intent0, 0);

        alarmMgr0.setRepeating(AlarmManager.RTC_WAKEUP, startMillis, millisDay, pendingIntent);
    }

}
