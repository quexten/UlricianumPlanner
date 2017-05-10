package com.quexten.ulricianumplanner.substitutions;

import org.jsoup.nodes.Element;

public class TableEntry {

    private String className;
    private String time;
    private String substituteTeacher;
    private String substituteSubject;
    private String room;
    private String type;
    private String switchedWith;
    private String teacher;
    private String subject;
    private String extraText;

    public TableEntry(String className, String time, String teacher, String subject, String type) {
        this.className = className;
        this.time = time;
        this.teacher = teacher;
        this.subject = subject;
        this.type = type;
    }

    public TableEntry(Element entry) {
        className = entry.child(0).html();
        time = entry.child(1).html();
        substituteTeacher = entry.child(2).html();
        substituteSubject = entry.child(3).html();
        room = entry.child(4).html();
        type = entry.child(5).html();
        switchedWith = entry.child(6).html();
        teacher = entry.child(7).html();
        subject = entry.child(8).html();
        extraText = entry.child(9).html();
    }

    public String getClassName() {
        return className;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubstituteTeacher() {
        return substituteTeacher;
    }

    public void setSubstituteTeacher(String substituteTeacher) {
        this.substituteTeacher = substituteTeacher;
    }

    public String getSubstituteSubject() {
        return substituteSubject;
    }

    public void setSubstituteSubject(String substituteSubject) {
        this.substituteSubject = substituteSubject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSwitchedWith() {
        return switchedWith;
    }

    public void setSwitchedWith(String switchedWith) {
        this.switchedWith = switchedWith;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    @Override
    public String toString() {
        return className + "/" + time + "/" + substituteTeacher + "/" + substituteSubject + "/" + room + "/" + type + "/" + switchedWith + "/" + teacher + "/" + subject + "/" + extraText;
    }

}
