package com.quexten.ulricianumplanner.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;

import com.quexten.ulricianumplanner.substitutions.Substitution;
import com.quexten.ulricianumplanner.sync.iserv.IServSubstitutionProvider;


class SyncAdapter extends AbstractThreadedSyncAdapter {

    Context context;
    SubstitutionProvider substitutionProvider;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        this.substitutionProvider = new IServSubstitutionProvider(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        this.substitutionProvider = new IServSubstitutionProvider(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        IServSubstitutionProvider substitutionProvider = new IServSubstitutionProvider(context);
        boolean sync = substitutionProvider.sync();
        if(sync) {
            for(Substitution substitution : substitutionProvider.getSubstitutions().asArray()){
                Intent intent = new Intent("com.quexten.ulricianumplanner.substitutionreceived");
                intent.putExtra("substitution", substitutionProvider.getNews().asStringArray());
                context.sendBroadcast(intent);
            }

            Intent intent = new Intent("com.quexten.ulricianumplanner.newsreceived");
            intent.putExtra("news", substitutionProvider.getNews().asStringArray());
            context.sendBroadcast(intent);
        }

        Intent intent = new Intent("com.quexten.ulricianumplanner.synced");
        intent.putExtra("com.quexten.ulricianumplanner.synced.sucessful", sync);
        context.sendBroadcast(intent);

        if(sync == false)
            syncResult.databaseError = true;
    }

}
