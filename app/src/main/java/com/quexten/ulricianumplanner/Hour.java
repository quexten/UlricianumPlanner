package com.quexten.ulricianumplanner;

/**
 * Created by Quexten on 03-Sep-16.
 */

public enum Hour {
    ONETWO, THREFOUR, FIVESIX, EIGHTNINE, TENELEVEN;
    
    public static Hour fromString(String string) {
        Hour hour = (string.equals("3") || string.equals("3 - 4") || string.equals("4")) ? Hour.THREFOUR : Hour.ONETWO;
        hour = (string.equals("5") || string.equals("5 - 6") || string.equals("6")) ? Hour.FIVESIX : hour;
        hour = (string.equals("8") || string.equals("8 - 9") || string.equals("9")) ? Hour.EIGHTNINE : hour;
        hour = (string.equals("10") || string.equals("10 - 11") || string.equals("10 - 12")|| string.equals("11") || string.equals("12")) ? Hour.TENELEVEN : hour;
        return hour;
    }
    
}
