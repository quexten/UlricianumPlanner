package com.quexten.ulricianumplanner;

/**
 * Created by Quexten on 03-Sep-16.
 */

public enum Hour {
    ONETWO, THREFOUR, FIVESIX, EIGHTNINE, TENELEVEN;
    
    public static Hour fromString(String string) {
        Hour hour = ("3".equals(string) || "3 - 4".equals(string) || "4".equals(string)) ? Hour.THREFOUR : Hour.ONETWO;
        hour = ("5".equals(string) || "5 - 6".equals(string) || "6".equals(string)) ? Hour.FIVESIX : hour;
        hour = ("8".equals(string) || "8 - 9".equals(string) || "9".equals(string) || "9 - 10".equals(string)) ? Hour.EIGHTNINE : hour;
        hour = ("10".equals(string) || "10 - 11".equals(string) || "10 - 12".equals(string)|| "11".equals(string) || "12".equals(string)) ? Hour.TENELEVEN : hour;
        return hour;
    }

    public static Hour fromInt(int integer) {
        Hour hour = integer == 0 ? Hour.ONETWO : integer == 1 ? Hour.THREFOUR: integer == 2 ? Hour.FIVESIX : integer == 3 ? Hour.EIGHTNINE : integer == 4 ? Hour.TENELEVEN : Hour.TENELEVEN;
        return hour;
    }

}
