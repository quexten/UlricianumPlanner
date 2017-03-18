package com.quexten.ulricianumplanner;

import android.content.Context;

/**
 * Created by Quexten on 03-Sep-16.
 */

public class Course {

    private String subject  = "";
    private String room  = "";
    private String teacher  = "";

    //WeekB
    private String subjectB = "";
    private String roomB  = "";
    private String teacherB  = "";


	public Course(String subject, String room, String teacher, String subjectB, String roomB, String teacherB) {
		this.subject = subject;
		this.room = room;
		this.teacher = teacher;
        this.subjectB = subjectB;
        this.roomB = roomB;
        this.teacherB = teacherB;
	}

    public Course(String subject, String room, String teacher) {
        this(subject, room, teacher, "", "", "");
    }
	
	@Override
	public String toString() {
		return subject+"|"+room+"|"+teacher;
	}


    public String getSubject() {
        return (subject != null) ? subject : "";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return (room != null) ? room : "";
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return (teacher != null) ? teacher : "";
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubjectB() {
        return (subjectB != null) ? subjectB : "";
    }

    public void setSubjectB(String subjectB) {
        this.subjectB = subjectB;
    }

    public String getRoomB() {
        return (roomB != null) ? roomB : "";
    }

    public void setRoomB(String roomB) {
        this.roomB = roomB;
    }

    public String getTeacherB() {
        return (teacherB != null) ? teacherB : "";
    }

    public void setTeacherB(String teacherB) {
        this.teacherB = teacherB;
    }

    public String getCurrentSubject() {
        return (hasDoubleWeekSchedule() && isWeekB()) ? getSubjectB() : getSubject();
    }

    public String getCurrentRoom() {
        return (hasDoubleWeekSchedule() && isWeekB()) ? getRoomB() : getRoom();
    }

    public String getCurrentTeacher() {
        return (hasDoubleWeekSchedule() && isWeekB()) ? getTeacherB() : getTeacher();
    }

    public boolean hasDoubleWeekSchedule() {
        return !this.getTeacherB().isEmpty();
    }

	public static String getLongSubjectName(Context context, String shortSubject) {
		String[] longStringArray = context.getResources().getStringArray(R.array.subjects_long);
		String[] shortStringArray = context.getResources().getStringArray(R.array.subjects);
		for(int i = 0; i < longStringArray.length; i++) {
			if(shortStringArray[i].equals(shortSubject))
				return longStringArray[i];
		}
		return shortSubject;
	}

	public static String getShortSubjectName(Context context, String longSubject) {
		String[] longStringArray = context.getResources().getStringArray(R.array.subjects_long);
		String[] shortStringArray = context.getResources().getStringArray(R.array.subjects);
		for(int i = 0; i < longStringArray.length; i++) {
			if(longStringArray[i].equals(longSubject))
				return shortStringArray[i];
		}
		return longSubject;
	}

	private static boolean isWeekB() {
        return false;
    }

}
