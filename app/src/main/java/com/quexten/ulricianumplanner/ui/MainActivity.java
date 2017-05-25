package com.quexten.ulricianumplanner.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.crash.internal.FirebaseCrashOptions;
import com.quexten.ulricianumplanner.BuildConfig;
import com.quexten.ulricianumplanner.FeedbackManager;
import com.quexten.ulricianumplanner.R;
import com.quexten.ulricianumplanner.RoomReceiver;
import com.quexten.ulricianumplanner.courseplan.Hour;
import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.news.NewsHandler;
import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.substitutions.SubstitutionHandler;
import com.quexten.ulricianumplanner.substitutions.SubstitutionType;
import com.quexten.ulricianumplanner.sync.SubscriptionManager;
import com.quexten.ulricianumplanner.substitutions.Substitutions;
import com.quexten.ulricianumplanner.courseplan.TeacherManager;
import com.quexten.ulricianumplanner.account.AccountManager;
import com.quexten.ulricianumplanner.courseplan.CoursePlan;

import java.util.Calendar;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {

    private CoursePlan coursePlan;
    private FeedbackManager feedbackManager;
    private Substitutions substitutions;
    private TimetableManager timetableManager;
    private TeacherManager teacherManager;

    private UiNewsBroadcastReceiver uiNewsBroadcastReceiver;

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
                //Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Do nothing
            }
        });

        AccountManager accountManager = new AccountManager(this.getApplicationContext());
        SubscriptionManager subscriptionManager = new SubscriptionManager(this.getApplicationContext());
        coursePlan = new CoursePlan(this.getApplicationContext(), subscriptionManager);
        feedbackManager = new FeedbackManager(this, coursePlan);
        teacherManager = new TeacherManager(this.getApplicationContext());
        substitutions = new SubstitutionHandler(this.getApplicationContext()).load();

        View myView = findViewById(R.id.pager);
        myView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(MainActivity.this.timetableManager == null) {
                    MainActivity.this.timetableManager = new TimetableManager(MainActivity.this, coursePlan, substitutions, teacherManager);
                    MainActivity.this.timetableManager.generateVisuals();
                    registerTimetableReceiver();

                    //Load news
                    News news = new NewsHandler(MainActivity.this).load();
                    setNews(news);
                }
            }
        });

        if(hasClassName())
            showClassSelection();

        if(!accountManager.hasAccount()) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        setBackgroundTasks();

        new TutorialManager(this);

        showAppRateDialog();

        coursePlan.read();
        subscriptionManager.subscribeToPlan(coursePlan);

        registerNewsListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //Debug Test Options
        if(BuildConfig.DEBUG) {
            menu.add("TestNews")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            String[] testNews = {"News item 1", "News item 2"};

                            Intent intent = new Intent("com.quexten.ulricianumplanner.newsreceived");
                            intent.putExtra("news", new News(testNews).asStringArray());
                            MainActivity.this.sendBroadcast(intent);
                            return false;
                        }
                    });
            menu.add("TestSubstiutiton")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Substitutions substitutions = new Substitutions();
                            Substitution testSubstitution = new Substitution();
                            testSubstitution.setDate(Calendar.getInstance().getTime());
                            testSubstitution.setHour(Hour.THREFOUR);
                            testSubstitution.setSubstitutionType(SubstitutionType.CANCELLED);
                            substitutions.add(testSubstitution);

                            for(Substitution substitution : substitutions.asArray()) {
                                Intent intent = new Intent("com.quexten.ulricianumplanner.substitutionreceived");
                                intent.putExtra("substitution", "");
                                MainActivity.this.sendBroadcast(intent);
                            }
                            return false;
                        }
                    });
        }
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

    private void showAppRateDialog() {
        AppRate.with(this)
                .setInstallDays(5)
                .setLaunchTimes(5)
                .setRemindInterval(1)
                .setShowLaterButton(true)
                .setDebug(false)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
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

    private void setBackgroundTasks() {
        //Room Tasks
        setDailyTask(7, 30, 3, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(9, 20, 4, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(11, 15, 5, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(13, 45, 6, new Intent(MainActivity.this, RoomReceiver.class));
        setDailyTask(15, 35, 7, new Intent(MainActivity.this, RoomReceiver.class));
    }

    private boolean hasClassName() {
        coursePlan.readClassName();
        return coursePlan.hasClassName();
    }

    private void showClassSelection() {
        //Class Selection Screen when no class is chosen yet
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
            public void onClick(DialogInterface dialog, int which) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNewsListener();
        unregisterTimetableReceiver();
    }

    private void registerNewsListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.quexten.ulricianumplanner.newsreceived");
        uiNewsBroadcastReceiver = new UiNewsBroadcastReceiver();
        registerReceiver(uiNewsBroadcastReceiver, filter);
    }

    private void unregisterNewsListener() {
        unregisterReceiver(uiNewsBroadcastReceiver);
    }

    private void registerTimetableReceiver() {
        timetableManager.registerSyncReceiver();
    }

    private void unregisterTimetableReceiver() {
        timetableManager.unregisterSyncReceiver();
    }

    public void setNews(News news) {
        LinearLayout newsView = ((LinearLayout) findViewById(R.id.news_layout));
        newsView.removeAllViews();

        for(String entry : news.asStringArray()) {
            View child = getLayoutInflater().inflate(R.layout.news_entry, null);
            ((TextView) child.findViewById(R.id.info_text)).setText(entry);
            newsView.addView(child);
        }
    }


}
