package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by Quexten on 09-Dec-16.
 */

class PostFeedbackTask extends AsyncTask<String, Boolean, Boolean> {

    private static final String FEEDBACK_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSd7xQld2bPmxe3UTkN6S5XWEz_IRgnWeA96zOy7R88f1tlYPg/formResponse";
    private static final String ENTRY_DESCRIPTION = "entry.1171372604";
    private static final String ENTRY_BUILDVERSION = "entry.996186014";
    private static final String ENTRY_DEVICEINFO = "entry.1210007803";
    private static final String ENTRY_TIMETABLE = "entry.1799750604";

    private Activity activity;
    private FeedbackManager.FeedbackOptions feedbackOptions;

    public PostFeedbackTask(Activity activity, FeedbackManager.FeedbackOptions feedbackOptions) {
        this.activity = activity;
        this.feedbackOptions = feedbackOptions;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(FEEDBACK_FORM_URL);

            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair(ENTRY_DESCRIPTION, feedbackOptions.getDescription()));
            postParameters.add(new BasicNameValuePair(ENTRY_BUILDVERSION, feedbackOptions.getBuildVersion()));
            postParameters.add(new BasicNameValuePair(ENTRY_DEVICEINFO, feedbackOptions.getDeviceInfo()));
            postParameters.add(new BasicNameValuePair(ENTRY_TIMETABLE, feedbackOptions.getTimetable()));
            request.setEntity(new UrlEncodedFormEntity(postParameters));

            httpClient.execute(request);
            postToast("Feedback wurde eingereicht, vielen Dank!");
        } catch(Exception ex) {
            ex.printStackTrace();
            postToast("Netzwerkfehler beim senden des Feedbacks");
        }
        return true;
    }

    private void postToast(String toast) {
        final String toastMessage = toast;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CharSequence text = toastMessage;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(activity, text, duration);
                    toast.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
