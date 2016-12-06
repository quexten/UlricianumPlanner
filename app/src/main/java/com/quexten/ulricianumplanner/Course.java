package com.quexten.ulricianumplanner;

import android.util.Log;

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
	
}
