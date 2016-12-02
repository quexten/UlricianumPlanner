package com.quexten.ulricianumplanner;

import org.jsoup.nodes.Element;

public class TableEntry {
			
	String className;
	String time;
	String substituteTeacher;
	String substituteSubject;
	String room;
	String type;
	String switchedWith;
	String teacher;
	String subject;
	String extraText;

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

	@Override
	public String toString() {
		return className + "/" + time + "/" + substituteTeacher + "/" + substituteSubject + "/" + room + "/" + type + "/" + switchedWith + "/" + teacher + "/" + subject + "/" + extraText;
	}
	
}
