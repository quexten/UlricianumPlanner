package com.quexten.ulricianumplanner.substitutions;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class Substitutions {

    ArrayList<Substitution> substitutions = new ArrayList<Substitution>();

    public Substitutions(Substitution[] substitutions) {
        for(Substitution substitution : substitutions)
            add(substitution);
    }

    public Substitutions() {
    }

    public void add(Substitution substitution) {
        //Remove substitutions in the same timeslot
        for(int i = 0; i < substitutions.size(); i++) {
            Substitution entry = substitutions.get(i);

            Calendar entryCalendar = Calendar.getInstance();
            entryCalendar.setTime(entry.getDate());
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(substitution.getDate());

            boolean sameDay = entryCalendar.get(Calendar.DAY_OF_WEEK) == newCalendar.get(Calendar.DAY_OF_WEEK);
            boolean sameMonth = entryCalendar.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH);
            boolean sameYear = entryCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR);

            if(sameDay && sameMonth && sameYear) {
                if(entry.getHour() == substitution.getHour()) {
                    substitutions.remove(i);
                }
            }
        }

        substitutions.add(substitution);
    }

    public Substitution[] asArray() {
        return substitutions.toArray(new Substitution[substitutions.size()]);
    }

    public void removeOldSubstitutions() {
        for(Substitution substitution : substitutions) {
            if(substitution.getDate().before(Calendar.getInstance().getTime())) {
                substitutions.remove(substitution);
            }
        }
    }

}
