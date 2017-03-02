package com.quexten.ulricianumplanner;

import android.*;
import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;


/**
 * Created by Quexten on 01-Dec-16.
 */

public class AccountManager {

    public static final String ACCOUNT_TYPE = "com.quexten.ulricianumplanner.account";
    public static final String PERMISSION_ACCOUNS = Manifest.permission.GET_ACCOUNTS;

    Context context;

    public AccountManager(Context context) {
        this.context = context;
    }

    public boolean addAccount(String username, String password) {
        Account account = new Account(username, ACCOUNT_TYPE);
        return android.accounts.AccountManager.get(context).addAccountExplicitly(account, password, new Bundle());
    }

    public boolean hasAccount() {
        return getUsername() != null;
    }

    public String getUsername() {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_ACCOUNS) == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = android.accounts.AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (account.type.equals(ACCOUNT_TYPE))
                    return account.name;
            }
        }
        return null;
    }

    public String getPassword() {
        if (ContextCompat.checkSelfPermission(context, PERMISSION_ACCOUNS) == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = android.accounts.AccountManager.get(context).getAccounts();
            for (Account account : accounts)
                if(account.type.equals(ACCOUNT_TYPE))
                    return android.accounts.AccountManager.get(context).getPassword(account);
        }

        return null;
    }



}
