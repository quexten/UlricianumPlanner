package com.quexten.ulricianumplanner.sync.iserv;

import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.substitutions.Substitutions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

/**
 * Created by Quexten on 23-May-17.
 */

public class IServ {

    private static final int MAX_SCANS = 10;

    private String endpointUrl;
    private String substitutionUrl;

    private IServCredentials credentials;

    public IServ (String endpointUrl, String substitutionUrl) {
        this.endpointUrl = endpointUrl;
        this.substitutionUrl = substitutionUrl;
    }

    public boolean login(String username, String password) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(endpointUrl);

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

            if(response.getStatusLine().getStatusCode() == 302) {
                //Parse IServ session information from HTTP headers
                String phpSessionId = null;
                String phpSessionPassword = null;

                for(Header header : response.getHeaders("Set-Cookie")) {
                    cz.msebera.android.httpclient.HeaderElement element = header.getElements()[0];
                    if(element.getName().equals("PHPSESSID"))
                        phpSessionId = element.getValue();
                    if(element.getName().equals("PHPSESSPW"))
                        phpSessionPassword = element.getValue();
                }

                this.credentials = new IServCredentials(phpSessionId, phpSessionPassword);
            }


            return response.getStatusLine().getStatusCode() == 302;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLoggedIn() {
        return this.credentials == null;
    }

    public SubstitutionPlan getPlan(boolean today) {
        IServPlanParser parser = new IServPlanParser();
        SubstitutionPlan substitutionPlan = new SubstitutionPlan();

        //Get first plan page to scrape metadata
        IServPlanParser.PlanPage planPage = parser.parseResponse(getPlanPage(today, 1));

        substitutionPlan.setNews(new News(planPage.getNews().split("\n")));

        substitutionPlan.setSubstitutions(new Substitutions());

        Date date = planPage.getDate();

        //Get total page count for scanning
        int pageCount = planPage.getPageNum();
        pageCount = Math.min(pageCount, MAX_SCANS);

        //Insert table entries
        List<TableEntry> entries = new ArrayList();
        entries.addAll(Arrays.asList(planPage.getEntries()));
        for(int i = 2; i < pageCount; i++)
            entries.addAll(Arrays.asList(parser.parseResponse(getPlanPage(today, i)).getEntries()));

        for(TableEntry entry : entries) {
            Substitution substitution = entry.toSubstitution();
            substitution.setDate(date);
            substitutionPlan.getSubstitutions().add(substitution);
        }

        return substitutionPlan;
    }

    private String getPlanPage(boolean today, int page) {
        String url = endpointUrl + substitutionUrl + (today ? "heute" : "morgen") + "/subst_00" + page + ".htm";

        IServPlanParser parser = new IServPlanParser();
        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpGet get = new HttpGet(url);
            get.setHeader("Cookie", "PHPSESSID=" + credentials.getPhpSessionId() + "; PHPSESSPW=" + credentials.getPhpSessionPassword());
            get.setHeader("User-Agent", "APP");
            HttpResponse response = client.execute(get);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Charset.forName("ISO-8859-15")));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
