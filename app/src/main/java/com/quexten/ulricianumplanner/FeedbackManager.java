package com.quexten.ulricianumplanner;

import android.app.Activity;

import android.os.Build;
import static com.quexten.ulricianumplanner.BuildConfig.VERSION_CODE;

/**
 * Created by Quexten on 09-Dec-16.
 */

public class FeedbackManager {

    private String DEVICE_INFO = Build.MANUFACTURER
            + " " + Build.MODEL
            + " " + Build.VERSION.RELEASE
            + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    private String BUILD_VERSION = String.valueOf(BuildConfig.VERSION_CODE);

    Activity activity;
    CoursePlan coursePlan;

    public FeedbackManager(Activity activity, CoursePlan coursePlan) {
        this.activity = activity;
        this.coursePlan = coursePlan;
    }

    public void submitFeedback(String description, boolean submitTimetable) {
        FeedbackOptions options = new FeedbackOptions()
                .description(description)
                .buildVersion(BUILD_VERSION)
                .deviceInfo(DEVICE_INFO)
                .timetable(submitTimetable ? coursePlan.toString() : null);
        uploadFeedback(options);
    }

    private void uploadFeedback(FeedbackOptions options) {
        new PostFeedbackTask(activity, options).execute();
    }

    class FeedbackOptions {

        private String buildVersion;
        private String description;
        private String deviceInfo;
        private String timetable;

        public FeedbackOptions() {
        }

        public FeedbackOptions buildVersion(String buildVersion) {
            this.buildVersion = buildVersion;
            return this;
        }

        public String getBuildVersion() {
            return buildVersion;
        }

        public FeedbackOptions description(String description) {
            this.description = description;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public FeedbackOptions deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public String getDeviceInfo() {
            return this.deviceInfo;
        }

        public FeedbackOptions timetable(String timetable) {
            this.timetable = timetable;
            return this;
        }

        public String getTimetable() {
            return timetable;
        }

    }

}
