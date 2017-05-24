package com.quexten.ulricianumplanner.sync.iserv;

/**
 * Created by Quexten on 23-May-17.
 */

public class IServCredentials {

    private String phpSessionId;
    private String phpSessionPassword;

    public IServCredentials(String phpSessionId, String phpSessionPassword) {
        this.phpSessionId = phpSessionId;
        this.phpSessionPassword = phpSessionPassword;
    }

    public void setPhpSessionId(String phpSessionId) {
        this.phpSessionId = phpSessionId;
    }

    public String getPhpSessionId() {
        return phpSessionId;
    }

    public void setPhpSessionPassword(String phpSessionPassword) {
        this.phpSessionPassword = phpSessionPassword;
    }

    public String getPhpSessionPassword() {
        return phpSessionPassword;
    }

}
