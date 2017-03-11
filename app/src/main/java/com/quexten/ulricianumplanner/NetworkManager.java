package com.quexten.ulricianumplanner;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class NetworkManager {

    //Constants
    static final String LOGIN_URL = "https://ulricianum-aurich.de/idesk/";

    //Session related info
    static String phpSessionId;
	static String phpSessionPassword;

    IServPlanParser parser;

    Context context;

    NewsListener newsListener;

    public NetworkManager(Context context) {
        this.context = context;
        this.parser = new IServPlanParser();
    }

    protected boolean login(String username, String password) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(LOGIN_URL);

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("login_act", username));
        postParameters.add(new BasicNameValuePair("login_pwd", password));
        UrlEncodedFormEntity form = null;
        try {
            form = new UrlEncodedFormEntity(postParameters);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(form);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            for(Header header : response.getHeaders("Set-Cookie")) {
                cz.msebera.android.httpclient.HeaderElement element = header.getElements()[0];
                if(element.getName().equals("PHPSESSID"))
                    phpSessionId = element.getValue();
                if(element.getName().equals("PHPSESSPW"))
                    phpSessionPassword = element.getValue();
            }
            return response.getStatusLine().getStatusCode() == 302;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isLoggedIn() {
        return phpSessionId != null;
    }

    /**
     * Fetches a list of current substitutions
     * this list is unfiltered, meaning that it does not account for
     * the users Course Plan
     * @return - the substitutions currently on the plan
     */
    public Substitutions getSubstitutions() {
        AccountManager accountManager = new AccountManager(context);
        if(!isLoggedIn()) {
            login(accountManager.getUsername(), accountManager.getPassword());
        }

        Substitutions substitutions = new Substitutions(context);

        ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();

        //Get today's substitutions
        int pageNum = 1;
        int todayScans = 0;
        while(todayScans < pageNum && todayScans < 10) { // todayScans < 10 is a safeguard against spamming the scan
            IServPlanParser.PlanPage planPage = getPlan(true, todayScans + 1);
            if(planPage == null) {
                return null;
            }
            for(TableEntry entry : planPage.getEntries())
                tableEntries.add(entry);

            //Set the number of pages found in the plan
            if(todayScans == 0) {
                substitutions.setTodayDay(planPage.getDay());
                pageNum = planPage.pageNum;
            }

            todayScans ++;
        }
        substitutions.setTodaySubstitutions(new TableEntry[tableEntries.size()]);
        for(int i = 0; i < tableEntries.size(); i++) {
            substitutions.getTodaySubstitutions()[i] = tableEntries.get(i);
        }

        tableEntries.clear();

        //Get tomorrow's substitutions
        pageNum = 1;
        int tomorrowScans = 0;
        while(tomorrowScans < pageNum && todayScans < 10) {
            IServPlanParser.PlanPage planPage = getPlan(false, tomorrowScans + 1);
            for(TableEntry entry : planPage.getEntries())
                tableEntries.add(entry);

            //Set the number of pages found in the plan
            if(tomorrowScans == 0) {
                substitutions.setTomorrowDay(planPage.getDay());
                pageNum = planPage.pageNum;
            }

            tomorrowScans ++;
        }
        substitutions.setTomorrowSubstitutions(new TableEntry[tableEntries.size()]);
        for(int i = 0; i < tableEntries.size(); i++) {
            substitutions.getTomorrowSubstitutions()[i] = tableEntries.get(i);
        }
        return substitutions;
    }

	private IServPlanParser.PlanPage getPlan(boolean today, int page) {
		String url = "https://ulricianum-aurich.de/idesk/plan/index.php/Vertretungsplan%20Sch%C3%BCler%20" + (today ? "heute" : "morgen") + "/subst_00" + page + ".htm";

        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet(url);
            get.setHeader("Cookie", "PHPSESSID=" + phpSessionId + "; PHPSESSPW=" + phpSessionPassword);
            get.setHeader("User-Agent", "APP");
            HttpResponse response = client.execute(get);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Charset.forName("ISO-8859-15")));
            String test = response.getEntity().toString();
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            String resultText = result.toString();
            IServPlanParser.PlanPage planPage = parser.parseResponse(resultText, today);
            if(today && planPage.news != null) {
                newsListener.newsReceived(planPage.news);
            }
            return planPage;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
	}

    public void setNewsListener(NewsListener newsListener) {
        this.newsListener = newsListener;
    }

}
