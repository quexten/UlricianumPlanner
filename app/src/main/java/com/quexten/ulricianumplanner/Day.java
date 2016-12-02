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

}
