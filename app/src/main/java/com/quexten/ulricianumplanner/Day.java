package com.quexten.ulricianumplanner;

import java.util.Calendar;

/**
 * Created by Quexten on 03-Sep-16.
 */

public enum Day {

    MON("mon"), TUE("tue"), WED("wed"), THU("thu"), FRI("fri"), SAT("sat"), SUN("sun");

    String name;

    Day(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Day fromInt(int integer) {
        switch(integer) {
            case 0:
                return Day.MON;
            case 1:
                return Day.TUE;
            case 2:
                return Day.WED;
            case 3:
                return Day.THU;
            case 4:
                return Day.FRI;
            case 5:
                return Day.SAT;
            case 6:
                return Day.SUN;
            default:
                return Day.MON;
        }
    }

    /**
     * Maps calendar day integers
     * @param day - the input day in calendar format
     * @return - the output day in 0-6 mon-sun scheme
     */
    public static int fromCalendarDay(int day) {
        switch(day) {
            case Calendar.MONDAY:
                return 0;
            case Calendar.TUESDAY:
                return 1;
            case Calendar.WEDNESDAY:
                return 2;
            case Calendar.THURSDAY:
                return 3;
            case Calendar.FRIDAY:
                return 4;
            case Calendar.SATURDAY:
                return 5;
            case Calendar.SUNDAY:
                return 6;
            default:
                return 0;
        }
    }

    public static Day fromString(String string) {
        return "mon".equalsIgnoreCase(string) ? Day.MON
                : "tue".equalsIgnoreCase(string) ? Day.TUE
                : "wed".equalsIgnoreCase(string) ? Day.WED
                : "thu".equalsIgnoreCase(string) ? Day.THU
                : "fri".equalsIgnoreCase(string) ? Day.FRI
                : "sat".equalsIgnoreCase(string) ? Day.SAT
                : "sun".equalsIgnoreCase(string) ? Day.SUN
                : Day.MON;
    }

}
