package com.quexten.ulricianumplanner;

/**
 * Created by Quexten on 03-Sep-16.
 */

public enum Day {
    MON("mon"), TUE("tue"), WED("wed"), THU("thu"), FRI("fri");

    String name;

    Day(String name) {
       this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Day fromInt(int integer) {
        Day day = integer == 0 ? Day.MON : integer == 1 ? Day.TUE : integer == 2 ? Day.WED : integer == 3 ? Day.THU : integer == 4 ? Day.FRI : Day.FRI;
        return day;
    }

    public static Day fromString(String string) {
        Day day = "mon".equalsIgnoreCase(string) ? Day.MON
                : "tue".equalsIgnoreCase(string) ? Day.TUE
                : "wed".equalsIgnoreCase(string) ? Day.WED
                : "thu".equalsIgnoreCase(string) ? Day.THU
                : "fri".equalsIgnoreCase(string) ? Day.FRI
                : Day.FRI;
        return day;
    }

}
