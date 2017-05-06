package com.quexten.ulricianumplanner.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.quexten.ulricianumplanner.NewsListener;
import com.quexten.ulricianumplanner.R;
import com.quexten.ulricianumplanner.Substitutions;
import com.quexten.ulricianumplanner.sync.SyncTask;
import com.quexten.ulricianumplanner.sync.SynchronizationListener;
import com.quexten.ulricianumplanner.TableEntry;
import com.quexten.ulricianumplanner.TeacherManager;
import com.quexten.ulricianumplanner.courseplan.Course;
import com.quexten.ulricianumplanner.courseplan.CoursePlan;
import com.quexten.ulricianumplanner.courseplan.Day;
import com.quexten.ulricianumplanner.courseplan.Hour;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;

/**
 * Created by Quexten on 01-Sep-16.
 */

public class TimetableManager {

    Activity activity;

    public CoursePlan coursePlan;
    public Substitutions substitutions;
    private TeacherManager teacherManager;

    public TimetableManager(final Activity activity, CoursePlan coursePlan, Substitutions substitutions, final NewsListener newsListener, TeacherManager teacherManager) {
        this.activity = activity;
        this.coursePlan = coursePlan;
        this.substitutions = substitutions;
        this.teacherManager = teacherManager;

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
                new SyncTask(activity, newsListener, new SynchronizationListener() {
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
        for(int x = 0; x < coursePlan.getCourses().length; x++) {
            for(int y = 0; y < coursePlan.getCourses()[0].length; y++) {
                Course course = coursePlan.getCourse(x, y);
                Day day = Day.fromInt(x);
                Hour hour = Hour.fromInt(y);
                LinearLayout layout = getTableEntryLayout(day, hour);
                if(layout == null) {
                    FirebaseCrash.log("Linear Timetable Layout is null."
                            + " Day:"  + day
                            + " Hour:" + hour);
                    break;
                }


                TextView subjectView = ((TextView) layout.findViewById(R.id.subjectView));
                TextView roomView = ((TextView) layout.findViewById(R.id.roomView));

                subjectView.setText(Course.getLongSubjectName(activity.getApplicationContext(), course.getSubject()));
                roomView.setText(course.getRoom());

                subjectView.setBackgroundColor(course.getSubject().isEmpty() ? Color.parseColor("#eeeeee") : Color.parseColor("#e0e0e0"));
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
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditingDialog(activity, TimetableManager.this, coursePlan, dayNumber, hourNumber);
                    }
                });
            }
        }

        for(TableEntry entry : (today ? substitutions.getTodaySubstitutions() : substitutions.getTomorrowSubstitutions())) {
            Day day = today ? substitutions.getTodayDay() : substitutions.getTomorrowDay();
            Hour hour = Hour.fromString(entry.getTime() == null ? "" : entry.getTime());

            LinearLayout layout = getTableEntryLayout(day, hour);

            TextView subjectView = ((TextView) layout.findViewById(R.id.subjectView));
            TextView roomView = ((TextView) layout.findViewById(R.id.roomView));

            if(!entry.getType().equals("Entfall"))
                subjectView.setText(Course.getLongSubjectName(activity.getApplicationContext(), entry.getSubstituteSubject()));

            if(entry.getType().equals("Vertret."))
                roomView.setText(entry.getSubstituteTeacher() + "-" + entry.getRoom());
            else
                roomView.setText(entry.getRoom());

            subjectView.setBackgroundColor(getColorForSubstitution(activity, entry.getType()));
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
                : substitution.equals("Raumä.") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_roomchange", -8336444)))
                : (substitution.equals("Vertret.") || substitution.equals("Betreu.") || substitution.equals("trotz A.")) ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_substitution", -8062)))
                : substitution.equals("Tausch") ? Color.parseColor(ColorPickerPreference.convertToARGB(sharedPref.getInt("color_swap", -7288071)))
                : Color.parseColor("#BCAAA4");
    }

    public View showEditingDialog(final Activity activity, final TimetableManager timetableManager, final CoursePlan coursePlan, final int dayNumber, final int hourNumber) {
        final Course selectedCourse = coursePlan.getCourse(dayNumber, hourNumber);

        final Course tempCourse = new Course(selectedCourse.getSubject(), selectedCourse.getRoom(), selectedCourse.getTeacher(),
                selectedCourse.getSubjectB(), selectedCourse.getRoomB(), selectedCourse.getTeacherB());

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setTitle(R.string.dialog_title);

        View child = activity.getLayoutInflater().inflate(R.layout.edit_dialog, null);
        final LinearLayout childLayout = ((LinearLayout) child);

        //View for Second Teacher Option, is initially hidden
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

        final AutoCompleteTextView teacherView = ((AutoCompleteTextView ) childLayout.findViewById(R.id.TeacherText));
        teacherView.setText(teacherManager.getFullTeacherName(selectedCourse.getTeacher()));
        teacherView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                if(focused) {
                    teacherView.setText(teacherManager.getTeacherShorthand(teacherView.getText().toString()));
                } else {
                    teacherView.setText(teacherManager.getFullTeacherName(teacherView.getText().toString()));
                }
            }
        });

        String[] shortTeacherList = teacherManager.getShorthandTeacherList();
        String[] fullTeacherList = teacherManager.getFullTeacherList();
        String[] autocompleteArray = new String[shortTeacherList.length];
        for(int i = 0; i < autocompleteArray.length; i++)
            autocompleteArray[i] = shortTeacherList[i] + " (" + fullTeacherList[i] + ")";
        teacherView.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, autocompleteArray));
        teacherView.setThreshold(1);
        teacherView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String)parent.getItemAtPosition(position);
                if(selection.contains(" "))
                    selection = selection.substring(0, selection.indexOf(" "));
                teacherView.setText(selection);
            }
        });

        final AutoCompleteTextView roomView = ((AutoCompleteTextView) childLayout.findViewById(R.id.RoomText));
        roomView.setText(selectedCourse.getRoom());

        final Spinner subjectSpinner = ((Spinner) childLayout.findViewById(R.id.SubjectSpinner));
        final String[] subjects = activity.getResources().getStringArray(R.array.subjects_long);

        //List of currently selected teachers subjects
        final ArrayList<String> teacherSubjects = new ArrayList<String>();
        if(!teacherView.getText().toString().isEmpty())
            for(String subject : teacherManager.getTeacherSubjects(selectedCourse.getCurrentTeacher()))
                teacherSubjects.add(Course.getLongSubjectName(activity, subject));

        teacherView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                teacherSubjects.clear();

                if(!teacherView.getText().toString().isEmpty()) {
                    String shorthand = teacherManager.getTeacherShorthand(charSequence.toString());
                    for (String subject : teacherManager.getTeacherSubjects(shorthand))
                        teacherSubjects.add(Course.getLongSubjectName(activity, subject.toString()));
                }

                String[] combinedArray = new String[subjects.length + teacherSubjects.size()];
                for(int i = 0; i < teacherSubjects.size(); i++)
                    combinedArray[i] = teacherSubjects.get(i);
                for(int i = teacherSubjects.size(); i < combinedArray.length; i++)
                    combinedArray[i] = subjects[i - teacherSubjects.size()];
                ColoredArrayAdapter dataAdapter = new ColoredArrayAdapter(activity, android.R.layout.simple_spinner_item, combinedArray);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectSpinner.setAdapter(dataAdapter);

                for(int i = teacherSubjects.size(); i < combinedArray.length; i++) {
                    dataAdapter.setColor(i, ResourcesCompat.getColor(activity.getResources(), R.color.colorTextSecondary, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        for(int i = 0; i < subjects.length; i++)
            if(subjects[i].equals(Course.getLongSubjectName(activity.getApplicationContext(), selectedCourse.getSubject()))) {
                subjectSpinner.setSelection(i);
                break;
            }

        final Switch abCycleSwitch = ((Switch) childLayout.findViewById(R.id.ab_weekcycle_switch));
        abCycleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    tempCourse.setTeacher(teacherView.getText().toString());
                    tempCourse.setSubject(subjectSpinner.getSelectedItem().toString());
                    tempCourse.setRoom(roomView.getText().toString());
                } else {
                    tempCourse.setTeacherB(teacherView.getText().toString());
                    tempCourse.setSubjectB(subjectSpinner.getSelectedItem().toString());
                    tempCourse.setRoomB(roomView.getText().toString());
                }

                String teacherText = checked ? tempCourse.getTeacherB() : tempCourse.getTeacher();
                String subjectText = checked ? tempCourse.getSubjectB() : tempCourse.getSubject();
                String roomText = checked ? tempCourse.getRoomB() : tempCourse.getRoom();
                teacherView.setText(teacherText);
                for(int i = 0; i < subjects.length; i++) {
                    if (subjects[i].equals(Course.getLongSubjectName(activity.getApplicationContext(), subjectText))) {
                        subjectSpinner.setSelection(i);
                        break;
                    }
                }
                roomView.setText(roomText);
            }
        });

        builderSingle.setView(child);

        builderSingle.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(timetableManager != null) {
                    Day day = Day.fromInt(dayNumber);
                    Hour hour = Hour.fromInt(hourNumber);

                    if(!abCycleSwitch.isChecked()) {
                        tempCourse.setTeacher(teacherView.getText().toString());
                        tempCourse.setSubject(Course.getShortSubjectName(activity, subjectSpinner.getSelectedItem().toString()));
                        tempCourse.setRoom(roomView.getText().toString());
                    } else {
                        tempCourse.setTeacherB(teacherView.getText().toString());
                        tempCourse.setSubjectB(Course.getShortSubjectName(activity, subjectSpinner.getSelectedItem().toString()));
                        tempCourse.setRoomB(roomView.getText().toString());
                    }

                    coursePlan.setCourse(day, hour, tempCourse);
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
