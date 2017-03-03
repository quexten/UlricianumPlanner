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

    public String getFullTeacherName(String shorthand) {
        shorthand = shorthand.toUpperCase();
        for(TeacherEntry entry : teacherList.list) {
            if(entry.shortName.toUpperCase().equals(shorthand))
                return entry.fullName;
        }
        return null;
    }

    public String getTeacherShorthand(String name) {
        for(TeacherEntry entry : teacherList.list) {
            if(entry.fullName.equals(name))
                return entry.shortName;
        }
        return null;
    }

}
