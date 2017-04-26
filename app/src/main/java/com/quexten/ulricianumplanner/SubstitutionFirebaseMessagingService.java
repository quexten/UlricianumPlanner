package com.quexten.ulricianumplanner;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Quexten on 21-Dec-16.
 */

public class SubstitutionFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        from = from.replaceAll("/topics/substitution-updates-", "");
        from = from.replace("O_", "Ö")
                .replace("o_", "ö")
                .replace("A_", "Ä")
                .replace("a_", "ä")
                .replace("U_", "Ü")
                .replace("u_", "ü");
        String[] entries = from.split("-");

        String day = entries[0];
        String time = entries[1]
                .replace("12", "1 - 2")
                .replace("34", "3 - 4")
                .replace("56", "5 - 6")
                .replace("78", "7 - 8")
                .replace("89", "8 - 9")
                .replace("910", "9 - 10")
                .replace("1011", "10 - 11")
                .replace("1112", "11 - 12")
                .replace("1012", "10 - 12");
        String teacher = entries[2];
        String subject = entries[3];

        String type = message.getData().get("type");
        String room = message.getData().get("room");
        String substituteSubject = message.getData().get("subject");
        String substituteTeacher = message.getData().get("teacher");

        TableEntry entry = new TableEntry("", time, teacher, subject, type);
        entry.setRoom(room);
        entry.setSubstituteSubject(substituteSubject.toUpperCase());
        entry.setSubstituteTeacher(substituteTeacher.toUpperCase());

        Substitutions substitutions = new Substitutions(this.getApplicationContext());
        substitutions.readSubstitutions();
        substitutions.insertSubstitution(entry, Day.fromString(day));
        substitutions.saveSubstitutions();
        TeacherManager teacherManager = new TeacherManager(getApplicationContext());
        new NotificationPoster(this.getApplicationContext(), teacherManager).postSubstitutionNotification(entry);
    }
}
