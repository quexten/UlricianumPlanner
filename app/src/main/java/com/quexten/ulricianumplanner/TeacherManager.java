package com.quexten.ulricianumplanner;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Quexten on 03-Mar-17.
 */

public class TeacherManager {

    private TeacherList teacherList;

    public TeacherManager(Context context) {
        teacherList = getTeacherList(context, R.raw.teachers);
    }

    /**
     * Gets the full teacher name for a given shorthand
     * @param shorthand - the shorthand
     * @return - the full name or the shorthand if none was found
     */
    public String getFullTeacherName(String shorthand) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.shortName.equalsIgnoreCase(shorthand))
                return entry.fullName;
        }
        return shorthand;
    }

    /**
     * Gets the shorthand for a full teacher name
     * @param name - the full name
     * @return - the shorthand or the full name if none was found
     */
    public String getTeacherShorthand(String name) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.fullName.equalsIgnoreCase(name))
                return entry.shortName;
        }
        return name;
    }

    /**
     * Gets the subjects for a given teacher
     * @param teacherShorthand - the teachers shorthand
     * @return a String array of the subjects, an empty String
     * array if no matching teacher is found
     */
    public String[] getTeacherSubjects(String teacherShorthand) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.shortName.equalsIgnoreCase(teacherShorthand))
                return entry.subjects;
        }
        return new String[0];
    }

    public String[] getShorthandTeacherList() {
        int size = teacherList.list.length;
        String[] list = new String[size];
        for(int i = 0; i < size; i++)
            list[i] =  teacherList.list[i].shortName;
        return list;
    }

    public String[] getFullTeacherList() {
        int size = teacherList.list.length;
        String[] list = new String[size];
        for(int i = 0; i < size; i++)
            list[i] =  teacherList.list[i].fullName;
        return list;
    }

    /**
     * Loads the teacher list from a specified resource
     * @param context - the context of the application
     * @param resourceId - the resource to load
     * @return - the populated TeacherList object
     */
    private TeacherList getTeacherList(Context context, int resourceId) {
        InputStream is = context.getResources().openRawResource(resourceId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return new Gson().fromJson(writer.toString(), TeacherList.class);
    }

}
