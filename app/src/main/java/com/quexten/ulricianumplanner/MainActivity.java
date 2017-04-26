package com.quexten.ulricianumplanner;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {

    AccountManager accountManager;
    SubscriptionManager subscriptionManager;
    CoursePlan coursePlan;
    FeedbackManager feedbackManager;
    NetworkManager networkManager;
    NotificationPoster notificationPoster;
    Substitutions substitutions;
    TimetableManager timetableManager;
    TeacherManager teacherManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_timetable)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_news)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        accountManager = new AccountManager(this.getApplicationContext());
        subscriptionManager = new SubscriptionManager(this.getApplicationContext());
        coursePlan = new CoursePlan(this.getApplicationContext(), subscriptionManager);
        feedbackManager = new FeedbackManager(this, coursePlan);
        networkManager = new NetworkManager(this.getApplicationContext());
        teacherManager = new TeacherManager(this.getApplicationContext());
        notificationPoster = new NotificationPoster(this.getApplicationContext(), teacherManager);
        substitutions = new Substitutions(this.getApplicationContext());

        View myView = findViewById(R.id.pager);
        myView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(MainActivity.this.timetableManager == null) {
                    NewsListener newsListener = new NewsListener(MainActivity.this.getApplicationContext()) {
                        @Override
                        public void newsReceived(String news) {
                            final String newsContent = news;

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout newsView = ((LinearLayout) MainActivity.this.findViewById(R.id.news_layout));
                                    newsView.removeAllViews();

                                    if(newsContent.isEmpty())
                                        return;

                                    String[] news = newsContent.split("\n");
                                    for(String entry : news) {
                                        View child = getLayoutInflater().inflate(R.layout.news_entry, null);
                                        ((TextView) child.findViewById(R.id.info_text)).setText(entry);
                                        newsView.addView(child);
                                    }
                                }
                            });
                            this.saveNews(news);
                        }
                    };
                    MainActivity.this.timetableManager = new TimetableManager(MainActivity.this, coursePlan, substitutions, newsListener, teacherManager);
                    newsListener.newsReceived(newsListener.loadNews());
                    MainActivity.this.timetableManager.generateVisuals();
                }
            }
        });


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
                    coursePlan.setClassName(arrayAdapter.getItem(which));
                    coursePlan.saveClassName();
                }
                });

            builderSingle.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    coursePlan.setClassName(arrayAdapter.getItem(which));
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

        //Room Tasks
        setDailyTask(7, 30, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(9, 20, 4, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(11, 15, 5, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(13, 45, 6, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(15, 35, 7, new Intent(MainActivity.this, RoomReceiver.class));

        setDailyTask(6, 00, 0, new Intent(MainActivity.this, SyncReceiver.class));
        setDailyTask(9, 30, 1, new Intent(MainActivity.this, SyncReceiver.class));
        setDailyTask(15, 30, 2, new Intent(MainActivity.this, SyncReceiver.class));

        new TutorialManager(this);

        AppRate.with(this)
                .setInstallDays(5)
                .setLaunchTimes(5)
                .setRemindInterval(1)
                .setShowLaterButton(true)
                .setDebug(false)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
        coursePlan.read();
        subscriptionManager.subscribeToPlan(coursePlan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
