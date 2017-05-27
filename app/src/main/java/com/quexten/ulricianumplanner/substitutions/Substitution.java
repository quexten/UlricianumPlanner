package com.quexten.ulricianumplanner.substitutions;

import com.quexten.ulricianumplanner.courseplan.Day;
import com.quexten.ulricianumplanner.courseplan.Hour;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quexten on 16-May-17.
 */

public class Substitution {

    SubstitutionType substitutionType;
    Date date;
    Hour hour;

    String substituteTeacher;
    String substituteSubject;
    String substituteRoom;

    public Substitution() {
    }

    public void setSubstitutionType(SubstitutionType substitutionType) {
        this.substitutionType = substitutionType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }

    public SubstitutionType getSubstitutionType() {
        return this.substitutionType;
    }

    public Date getDate() {
        return date;
    }

    public Day getDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate());
        return Day.fromInt(Day.fromCalendarDay(calendar.get(Calendar.DAY_OF_WEEK)));
    }

    public Hour getHour() {
        return hour;
    }
}
