package com.quexten.ulricianumplanner.sync.iserv;

import android.content.Context;

import com.quexten.ulricianumplanner.account.AccountManager;
import com.quexten.ulricianumplanner.news.News;
import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.substitutions.SubstitutionHandler;
import com.quexten.ulricianumplanner.substitutions.Substitutions;
import com.quexten.ulricianumplanner.sync.SubstitutionProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Quexten on 15-May-17.
 */
public class IServSubstitutionProvider implements SubstitutionProvider {

    //Constants
    public static final String ENDPOINT_URL = "https://ulricianum-aurich.de/idesk/";
    public static final String SUBSTITUTION_URL = "plan/index.php/Vertretungsplan%20Sch%C3%BCler%20";

    //Android context
    private Context context;

    //Received data
    Substitutions substitutions;
    News news;

    public IServSubstitutionProvider(Context context) {
        this.context = context;
    }

    @Override
    public boolean sync() {
        IServ iServ = new IServ(ENDPOINT_URL, SUBSTITUTION_URL);

        AccountManager accountManager = new AccountManager(context);
        boolean isLoginSuccessful = iServ.login(accountManager.getUsername(), accountManager.getPassword());

        //Check if login failed
        if(!isLoginSuccessful) {
            return false;
        }

        SubstitutionPlan todayPlan = iServ.getPlan(false);
        SubstitutionPlan tomorrowPlan = iServ.getPlan(true);

        Substitution[] todaySubstitutionsArray = todayPlan.getSubstitutions().asArray();
        Substitution[] tomorrowSubstitutionsArray = tomorrowPlan.getSubstitutions().asArray();

        Substitution[] mergedSubstitutionArray = new Substitution[todaySubstitutionsArray.length + tomorrowSubstitutionsArray.length];
        for(int i = 0; i < todaySubstitutionsArray.length; i++)
            mergedSubstitutionArray[i] = todaySubstitutionsArray[i];
        for(int i = todaySubstitutionsArray.length; i < mergedSubstitutionArray.length; i++)
            mergedSubstitutionArray[i] = tomorrowSubstitutionsArray[i - todaySubstitutionsArray.length];

        Substitutions mergedSubstitutions = new Substitutions(mergedSubstitutionArray);
        this.substitutions = mergedSubstitutions;

        this.news = todayPlan.getNews();

        return true;
    }

    @Override
    public Substitutions getSubstitutions() {
        return this.substitutions;
    }

    @Override
    public News getNews() {
        return this.news;
    }

}
