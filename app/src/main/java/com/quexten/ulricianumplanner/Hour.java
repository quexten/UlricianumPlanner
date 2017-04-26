package com.quexten.ulricianumplanner;

/**
 * Created by Quexten on 03-Sep-16.
 */

public enum Hour {

    ONETWO, THREFOUR, FIVESIX, EIGHTNINE, TENELEVEN;
    
    public static Hour fromString(String string) {
        switch(string) {
            case "3":
            case "4":
            case "3 - 4":
                return Hour.THREFOUR;
            case "5":
            case "6":
            case "5 - 6":
                return Hour.FIVESIX;
            case "8":
            case "9":
            case "8 - 9":
            case "9 - 10":
                return Hour.EIGHTNINE;
            case "10":
            case "11":
            case "12":
            case "10 - 11":
            case "10 - 12":
                return Hour.TENELEVEN;
            default:
                return Hour.ONETWO;
        }
    }

    public static Hour fromInt(int integer) {
        Hour hour = integer == 0 ? Hour.ONETWO
                : integer == 1 ? Hour.THREFOUR
                : integer == 2 ? Hour.FIVESIX
                : integer == 3 ? Hour.EIGHTNINE
                : integer == 4 ? Hour.TENELEVEN
                : Hour.TENELEVEN;
        return hour;
    }

}
