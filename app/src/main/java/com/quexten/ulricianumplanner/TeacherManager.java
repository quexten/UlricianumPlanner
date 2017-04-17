package com.quexten.ulricianumplanner;

import android.content.Context;
import android.util.Log;

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

    TeacherList teacherList;

    public TeacherManager(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.teachers);
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

        String jsonString = writer.toString();
        teacherList = new Gson().fromJson(jsonString, TeacherList.class);
    }

    /**Gets the full teacher name for a given shorthand
     * @param shorthand - the shorthand
     * @return - the full name or the shorthand if none was found
     */
    public String getFullTeacherName(String shorthand) {
        shorthand = shorthand.toUpperCase();
        for(TeacherEntry entry : teacherList.list) {
            if(entry.shortName.toUpperCase().equals(shorthand))
                return entry.fullName;
        }
        return null;
    }

    /**Gets the shorthand for a full teacher name
     * @param name - the full name
     * @return - the shorthand or the full name if none was found
     */
    public String getTeacherShorthand(String name) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.fullName.equals(name))
                return entry.shortName;
        }
        return null;
    }

    /**Gets the subjects for a given teacher
     * @param teacherShorthand - the teachers shorthand
     * @return a String array of the subjects, an empty String
     * array if no matching teacher is found
     */
    public String[] getTeacherSubjects(String teacherShorthand) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.shortName.toUpperCase().equals(teacherShorthand.toUpperCase()))
                return entry.subjects;
        }
        return new String[0];
    }

}
