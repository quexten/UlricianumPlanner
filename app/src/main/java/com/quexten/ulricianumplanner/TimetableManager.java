package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Quexten on 01-Sep-16.
 */

public class TimetableManager {

    Activity activity;

    public CoursePlan coursePlan;
    public Substitutions substitutions;

    public TimetableManager(final Activity activity, CoursePlan coursePlan, Substitutions substitutions) {
        this.activity = activity;
        this.coursePlan = coursePlan;
        this.substitutions = substitutions;

        final SwipeRefreshLayout swipeRefreshLayout = ((SwipeRefreshLayout) activity.findViewById(R.id.swiperefreshlayout));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                new SyncTask(activity, new Runnable() {
                    @Override
                    public void run() {
                        TimetableManager.this.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TimetableManager.this.substitutions.readSubstitutions();
                                TimetableManager.this.generateVisuals();
                                try {
                                    CharSequence text = activity.getResources().getString(R.string.synced);
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(TimetableManager.this.activity.getApplicationContext(), text, duration);
                                    toast.show();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }).execute();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(activity.getResources().getColor(R.color.colorPrimary));
    }

    public void generateVisuals() {
        coursePlan.read();

        //Clear Backgrounds
        for(int x = 0; x < coursePlan.courseArray.length; x++) {
            for(int y = 0; y < coursePlan.courseArray[0].length; y++) {
                Course course = coursePlan.getCourse(x, y);
                Day day = Day.fromInt(x);
                Hour hour = Hour.fromInt(y);
                LinearLayout layout = getTableEntryLayout(day, hour);
                TextView subjectView = ((TextView) layout.findViewById(R.id.subjectView));
                TextView roomView = ((TextView) layout.findViewById(R.id.roomView));

                subjectView.setText(course.subject);
                roomView.setText(course.room);

                subjectView.setBackgroundColor(course.subject.isEmpty() ? Color.parseColor("#eeeeee") : Color.parseColor("#e0e0e0"));
            }
        }

        //Highlight Plan Days
        if(substitutions.getTodayDay() != null) {
            int dayBackgroundColor = Color.parseColor("#eeeeee");
            int currentPlanDayBackgroundColor = Color.parseColor("#bdbdbd");

            getViewForDay(Day.MON).setBackgroundColor(dayBackgroundColor);
            getViewForDay(Day.TUE).setBackgroundColor(dayBackgroundColor);
            getViewForDay(Day.WED).setBackgroundColor(dayBackgroundColor);
            getViewForDay(Day.THU).setBackgroundColor(dayBackgroundColor);
            getViewForDay(Day.FRI).setBackgroundColor(dayBackgroundColor);

            Day currentDay = substitutions.getTodayDay();
            Day tomorrowDay = substitutions.getTomorrowDay();

            getViewForDay(currentDay).setBackgroundColor(currentPlanDayBackgroundColor);
            getViewForDay(tomorrowDay).setBackgroundColor(currentPlanDayBackgroundColor);
        }

        //Visualize Overrides
        visualizeOverrides(false);
        visualizeOverrides(true);
    }

    void visualizeOverrides(boolean today) {
        Day[] days = new Day[]{Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI};
        Hour[] hours = new Hour[]{Hour.ONETWO, Hour.THREFOUR, Hour.FIVESIX, Hour.EIGHTNINE, Hour.TENELEVEN};

        for(Day day: days) {
            for(Hour hour: hours) {
                final int dayNumber = day.ordinal();
                final int hourNumber = hour.ordinal();

                LinearLayout layout = getTableEntryLayout(day, hour);
                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Course selectedCourse = coursePlan.getCourse(dayNumber, hourNumber);

                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
                        builderSingle.setTitle(R.string.dialog_title);

                        View child = activity.getLayoutInflater().inflate(R.layout.edit_dialog, null);
                        LinearLayout childLayout = ((LinearLayout) child);

                        builderSingle.setNeutralButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Day day = Day.fromInt(dayNumber);
                                Hour hour = Hour.fromInt(hourNumber);
                                coursePlan.setCourse(day, hour, new Course("", "", ""));
                                coursePlan.save();
                                TimetableManager.this.generateVisuals();
                            }
                        });

                        final AutoCompleteTextView teacherView = ((AutoCompleteTextView ) childLayout.findViewById(R.id.TeacherText));
                        teacherView.setText(selectedCourse.teacher);
                        teacherView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


                        final AutoCompleteTextView roomView = ((AutoCompleteTextView) childLayout.findViewById(R.id.RoomText));
                        roomView.setText(selectedCourse.room);

                        final Spinner subjectSpinner = ((Spinner) childLayout.findViewById(R.id.SubjectSpinner));
                        String[] subjects = activity.getResources().getStringArray(R.array.subjects);
                        for(int i = 0; i < subjects.length; i++)
                            if(subjects[i].equals(selectedCourse.subject)) {
                                subjectSpinner.setSelection(i);
                                break;
                            }

                        builderSingle.setView(child);

                        builderSingle.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Day day = Day.fromInt(dayNumber);
                                Hour hour = Hour.fromInt(hourNumber);
                                coursePlan.setCourse(day, hour, new Course(subjectSpinner.getSelectedItem().toString(), roomView.getText().toString(), teacherView.getText().toString()));
                                coursePlan.save();
                                TimetableManager.this.generateVisuals();
                            }
                        });
                        builderSingle.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builderSingle.show();


                        return false;
                    }
                });
            }
        }

        for(TableEntry entry : (today ? substitutions.getTodaySubstitutions() : substitutions.getTomorrowSubstitutions())) {
            Day day = today ? substitutions.getTodayDay() : substitutions.getTomorrowDay();
            Hour hour = Hour.fromString(entry.time);

            LinearLayout layout = getTableEntryLayout(day, hour);

            TextView subjectView = ((TextView) layout.findViewById(R.id.subjectView));
            TextView roomView = ((TextView) layout.findViewById(R.id.roomView));

            if(!entry.type.equals("Entfall"))
                subjectView.setText(entry.substituteSubject);

            if(entry.type.equals("Vertret."))
                roomView.setText(entry.substituteTeacher + "-" + entry.room);
            else
                roomView.setText(entry.room);

            subjectView.setBackgroundColor(entry.type.equals("Entfall") ? Color.parseColor("#ef9a9a")
                                        : entry.type.equals("Verleg.") ? Color.parseColor("#B39DDB")
                                        : entry.type.equals("Raum�.") ? Color.parseColor("#80CBC4")
                                        : (entry.type.equals("Vertret.") || entry.type.equals("Betreu.") || entry.type.equals("trotz A.")) ? Color.parseColor("#FFE082")
                                        : entry.type.equals("Tausch") ? Color.parseColor("#90CAF9")
                                        : Color.parseColor("#BCAAA4"));
        }
    }

    LinearLayout getTableEntryLayout(Day day, Hour hour) {
        switch(day) {
            case MON:
                switch(hour) {
                    case ONETWO:
                        return ((LinearLayout) activity.findViewById(R.id.mon12));
                    case THREFOUR:
                        return ((LinearLayout) activity.findViewById(R.id.mon34));
                    case FIVESIX:
                        return ((LinearLayout) activity.findViewById(R.id.mon56));
                    case EIGHTNINE:
                        return ((LinearLayout) activity.findViewById(R.id.mon89));
                    case TENELEVEN:
                        return ((LinearLayout) activity.findViewById(R.id.monAB));
                }
            break;
            case TUE:
                switch(hour) {
                    case ONETWO:
                        return ((LinearLayout) activity.findViewById(R.id.tue12));
                    case THREFOUR:
                        return ((LinearLayout) activity.findViewById(R.id.tue34));
                    case FIVESIX:
                        return ((LinearLayout) activity.findViewById(R.id.tue56));
                    case EIGHTNINE:
                        return ((LinearLayout) activity.findViewById(R.id.tue89));
                    case TENELEVEN:
                        return ((LinearLayout) activity.findViewById(R.id.tueAB));
                }
                break;
            case WED:
                switch(hour) {
                    case ONETWO:
                        return ((LinearLayout) activity.findViewById(R.id.wed12));
                    case THREFOUR:
                        return ((LinearLayout) activity.findViewById(R.id.wed34));
                    case FIVESIX:
                        return ((LinearLayout) activity.findViewById(R.id.wed56));
                    case EIGHTNINE:
                        return ((LinearLayout) activity.findViewById(R.id.wed89));
                    case TENELEVEN:
                        return ((LinearLayout) activity.findViewById(R.id.wedAB));
                }
                break;
            case THU:
                switch(hour) {
                    case ONETWO:
                        return ((LinearLayout) activity.findViewById(R.id.thu12));
                    case THREFOUR:
                        return ((LinearLayout) activity.findViewById(R.id.thu34));
                    case FIVESIX:
                        return ((LinearLayout) activity.findViewById(R.id.thu56));
                    case EIGHTNINE:
                        return ((LinearLayout) activity.findViewById(R.id.thu89));
                    case TENELEVEN:
                        return ((LinearLayout) activity.findViewById(R.id.thuAB));
                }
                break;
            case FRI:
                switch(hour) {
                    case ONETWO:
                        return ((LinearLayout) activity.findViewById(R.id.fri12));
                    case THREFOUR:
                        return ((LinearLayout) activity.findViewById(R.id.fri34));
                    case FIVESIX:
                        return ((LinearLayout) activity.findViewById(R.id.fri56));
                    case EIGHTNINE:
                        return ((LinearLayout) activity.findViewById(R.id.fri89));
                    case TENELEVEN:
                        return ((LinearLayout) activity.findViewById(R.id.friAB));
                }
                break;

        }
        return null;
    }

    private View getView() {
        return LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.tableentry, null);
    }

    private TextView getViewForDay(Day day) {
        int viewId = 0;
        switch(day) {
            case MON:
                viewId = R.id.day_monday;
                break;
            case TUE:
                viewId = R.id.day_tuesday;
                break;
            case WED:
                viewId = R.id.day_wednesday;
                break;
            case THU:
                viewId = R.id.day_thursday;
                break;
            case FRI:
                viewId = R.id.day_friday;
                break;
        }
        return (TextView) activity.findViewById(viewId);
    }

}
