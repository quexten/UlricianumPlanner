package com.quexten.ulricianumplanner;

import android.content.Context;

/**
 * Created by Quexten on 03-Sep-16.
 */

public class Course {

	String subject;
	String room;
	String teacher;

	public Course(String subject, String room, String teacher) {
		this.subject = subject;
		this.room = room;
		this.teacher = teacher;
	}
	
	@Override
	public String toString() {
		return subject+"|"+room+"|"+teacher;
	}

	public String[] getTeachers() {
		return teacher.split("\\s");
	}

	public static String getLongSubjectName(Context context, String shortSubject) {
		String[] longStringArray = context.getResources().getStringArray(R.array.subjects_long);
		String[] shortStringArray = context.getResources().getStringArray(R.array.subjects);
		for(int i = 0; i < longStringArray.length; i++) {
			if(shortStringArray[i].equals(shortSubject))
				return longStringArray[i];
		}
		return " ";
	}

	public static String getShortSubjectName(Context context, String longSubject) {
		String[] longStringArray = context.getResources().getStringArray(R.array.subjects_long);
		String[] shortStringArray = context.getResources().getStringArray(R.array.subjects);
		for(int i = 0; i < longStringArray.length; i++) {
			if(longStringArray[i].equals(longSubject))
				return shortStringArray[i];
		}
		return " ";
	}

}
