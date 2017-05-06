package com.quexten.ulricianumplanner.account;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Quexten on 01-Dec-16.
 */

public class AccountManager {

    public static final String ACCOUNT_TYPE = "com.quexten.ulricianumplanner.account";

    private android.accounts.AccountManager accountManager;

    public AccountManager(Context context) {
        accountManager = android.accounts.AccountManager.get(context);
    }

    public boolean addAccount(String username, String password) {
        Account account = new Account(username, ACCOUNT_TYPE);
        return accountManager
                .addAccountExplicitly(account, password, new Bundle());
    }

    public boolean hasAccount() {
        return getUsername() != null;
    }

    public String getUsername() {
        Account[] accounts = accountManager.getAccounts();
        for (Account account : accounts) {
            if (account.type.equals(ACCOUNT_TYPE))
                return account.name;
        }
        return null;
    }

    public String getPassword() {
        Account[] accounts = accountManager.getAccounts();
        for (Account account : accounts)
            if(account.type.equals(ACCOUNT_TYPE))
                return accountManager.getPassword(account);
        return null;
    }

}
