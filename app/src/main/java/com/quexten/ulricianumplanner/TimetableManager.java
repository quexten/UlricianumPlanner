package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

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
                new SyncTask(activity, new SynchronizationListener() {
                    @Override
                    public void onSync(boolean successful) {
                        final boolean wasSuccessful = successful;

                        TimetableManager.this.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TimetableManager.this.substitutions.readSubstitutions();
                                TimetableManager.this.generateVisuals();
                                try {
                                    int text = wasSuccessful ? R.string.synced : R.string.no_internet_connection;
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

                subjectView.setText(Course.getLongSubjectName(activity.getApplicationContext(), course.subject));
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
                        TimetableManager.showEditingDialog(activity, TimetableManager.this, coursePlan, dayNumber, hourNumber);
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
                subjectView.setText(Course.getLongSubjectName(activity.getApplicationContext(), entry.substituteSubject));

            if(entry.type.equals("Vertret."))
                roomView.setText(entry.substituteTeacher + "-" + entry.room);
            else
                roomView.setText(entry.room);

            subjectView.setBackgroundColor(getColorForSubstitution(activity, entry.type));
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

    public static int getColorForSubstitution(Context context, String substitution) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return substitution.equals("Entfall") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_cancelled", -1074534)))
                : substitution.equals("Verleg.") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_delayed", -5005861)))
                : substitution.equals("Raum�.") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_roomchange", -8336444)))
                : substitution.equals("Raumä.") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_roomchange", -8336444)))
                : (substitution.equals("Vertret.") || substitution.equals("Betreu.") || substitution.equals("trotz A.")) ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_substitution", -8062)))
                : substitution.equals("Tausch") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_swap", -7288071)))
                : Color.parseColor("#BCAAA4");
    }

    public static View showEditingDialog(final Activity activity, final TimetableManager timetableManager, final CoursePlan coursePlan, final int dayNumber, final int hourNumber) {
        final Course selectedCourse = coursePlan.getCourse(dayNumber, hourNumber);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setTitle(R.string.dialog_title);

        View child = activity.getLayoutInflater().inflate(R.layout.edit_dialog, null);
        final LinearLayout childLayout = ((LinearLayout) child);

        //View for Second Teacher Option, is initially hidden
        final LinearLayout teacherLayout = ((LinearLayout) childLayout.findViewById(R.id.teacher_text_layout));
        final EditText secondTeacherView = new EditText(activity);
        secondTeacherView.setHint("Lehrer");
        secondTeacherView.setAllCaps(true);
        secondTeacherView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        secondTeacherView.setEms(10);
        secondTeacherView.setLayoutParams(new DrawerLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        secondTeacherView.setEnabled(true);
        secondTeacherView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


        builderSingle.setNeutralButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(timetableManager != null) {
                    Day day = Day.fromInt(dayNumber);
                    Hour hour = Hour.fromInt(hourNumber);
                    coursePlan.setCourse(day, hour, new Course("", "", ""));
                    coursePlan.save();
                    timetableManager.generateVisuals();
                }
            }
        });

        final ImageButton addButton = ((ImageButton) childLayout.findViewById(R.id.add_teacher_button));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secondTeacherView.getParent() == null) {
                    addButton.setBackgroundResource(R.drawable.ic_action_delete);
                    secondTeacherView.setText("");
                    teacherLayout.addView(secondTeacherView, 1);
                } else {
                    addButton.setBackgroundResource(R.drawable.ic_action_add);
                    teacherLayout.removeView(secondTeacherView);
                }
            }
        });

        if(selectedCourse.getTeachers().length > 1) {
            teacherLayout.addView(secondTeacherView, 1);
            secondTeacherView.setText(selectedCourse.getTeachers()[1]);
            addButton.setBackgroundResource(R.drawable.ic_action_delete);
        }

        final TextView teacherView = ((TextView ) childLayout.findViewById(R.id.TeacherText));
        teacherView.setText(selectedCourse.getTeachers()[0]);
        teacherView.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        final AutoCompleteTextView roomView = ((AutoCompleteTextView) childLayout.findViewById(R.id.RoomText));
        roomView.setText(selectedCourse.room);

        final Spinner subjectSpinner = ((Spinner) childLayout.findViewById(R.id.SubjectSpinner));
        String[] subjects = activity.getResources().getStringArray(R.array.subjects_long);
        for(int i = 0; i < subjects.length; i++)
            if(subjects[i].equals(Course.getLongSubjectName(activity.getApplicationContext(), selectedCourse.subject))) {
                subjectSpinner.setSelection(i);
                break;
            }

        builderSingle.setView(child);

        builderSingle.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(timetableManager != null) {
                    Day day = Day.fromInt(dayNumber);
                    Hour hour = Hour.fromInt(hourNumber);
                    String teacherString = teacherView.getText().toString() + (secondTeacherView.getParent() != null ? (" " + secondTeacherView.getText()) : "");
                    coursePlan.setCourse(day, hour, new Course(Course.getShortSubjectName(activity.getApplicationContext(), subjectSpinner.getSelectedItem().toString()), roomView.getText().toString(), teacherString));
                    coursePlan.save();
                    timetableManager.generateVisuals();
                }
            }
        });
        builderSingle.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builderSingle.show();
        return child;
    }

}
