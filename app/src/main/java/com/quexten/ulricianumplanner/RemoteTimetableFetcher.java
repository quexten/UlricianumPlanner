package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Quexten on 14-Dec-16.
 */

public class RemoteTimetableFetcher {

    private static final String FTP_URL = "ulricianum-aurich.de";
    private static final String TIMETABLE_FILE_PATH = "timetable.json";
    private static final String PREFERENCE_DATE_KEY = "remote_timetable_modification_date";
    private static final String DEFAULT_DATE = "19700101000000";

    Activity activity;
    Context context;
    AccountManager accountManager;
    CoursePlan coursePlan;
    TimetableManager timetableManager;

    FTPClient ftpClient;
    Date remoteFileModificationDate;

    public RemoteTimetableFetcher(final Activity activity, AccountManager accountManager, CoursePlan coursePlan, TimetableManager timetableManager) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.accountManager = accountManager;
        this.coursePlan = coursePlan;
        this.timetableManager = timetableManager;

        if(!login()) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(RemoteTimetableFetcher.this.activity.getApplicationContext(), activity.getResources().getString(R.string.login_unsuccessful), duration);
            toast.show();
        }
        if(hasFile(TIMETABLE_FILE_PATH)) {
            remoteFileModificationDate = getModificationTime(TIMETABLE_FILE_PATH);

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            Date localTimetableDate = getDateFromString(sharedPref.getString(PREFERENCE_DATE_KEY, DEFAULT_DATE));
            final String remoteTimetableContent = getFileContents(TIMETABLE_FILE_PATH);

            if(remoteFileModificationDate.after(localTimetableDate)) {
                //Show Question Dialog
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Gson gson = new Gson();
                                        RemoteTimetable remoteTimetable = gson.fromJson(remoteTimetableContent, RemoteTimetable.class);
                                        RemoteTimetableFetcher.this.coursePlan.className = remoteTimetable.className;
                                        for(int day = 0; day < 5; day++) {
                                            for(int hour = 0; hour < 5; hour++) {
                                                Course course = remoteTimetable.timetable[day][hour];
                                                RemoteTimetableFetcher.this.coursePlan.setCourse(Day.fromInt(day), Hour.fromInt(hour), course != null ? course : new Course("", "", ""));
                                            }
                                        }
                                        RemoteTimetableFetcher.this.coursePlan.saveClassName();
                                        RemoteTimetableFetcher.this.coursePlan.save();
                                        if(RemoteTimetableFetcher.this.timetableManager != null)
                                            RemoteTimetableFetcher.this.timetableManager.generateVisuals();
                                        sharedPref.edit()
                                                .putString(PREFERENCE_DATE_KEY, getStringFromDate(remoteFileModificationDate))
                                                .commit();
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        String title = context.getResources().getString(R.string.fetch_timetable_dialog_title);
                        String description = context.getResources().getString(R.string.fetch_timetable_dialog_description);
                        String negativeOption = context.getResources().getString(R.string.dialog_negative);
                        String positiveOption = context.getResources().getString(R.string.dialog_positive);

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(title)
                               .setMessage(description)
                               .setPositiveButton(positiveOption, dialogClickListener)
                               .setNegativeButton(negativeOption, dialogClickListener)
                               .create()
                               .show();
                    }
                });
            }
        }
        logout();
    }


    private boolean login() {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(InetAddress.getByName(FTP_URL));
            if(!ftpClient.login(accountManager.getUsername(), accountManager.getPassword()))
                return false;
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            ftpClient.enterLocalPassiveMode();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void logout() {
        try {
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getFileContents(String path) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ftpClient.retrieveFile(path, byteArrayOutputStream);
            return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean hasFile(String path) {
        try {
            FTPFile[] files = ftpClient.listFiles();
            for(FTPFile file : files)
                if(file.getName().equals(path))
                    return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Date getModificationTime(String path) {
        try {
            String timePart = ftpClient.getModificationTime(path);
            return getDateFromString(timePart);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Date getDateFromString(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return dateFormat.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getStringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    private class RemoteTimetable {
        String className;
        Course[][] timetable;
    }

}
