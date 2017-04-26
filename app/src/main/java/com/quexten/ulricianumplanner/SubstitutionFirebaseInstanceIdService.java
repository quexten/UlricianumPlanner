package com.quexten.ulricianumplanner;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Quexten on 21-Dec-16.
 */

public class SubstitutionFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        //Do nothing, tokens aren't used
    }
}
