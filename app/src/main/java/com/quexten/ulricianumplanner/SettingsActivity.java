package com.quexten.ulricianumplanner;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);

        SubscriptionManager subscriptionManager = new SubscriptionManager(this.getApplicationContext());
        final CoursePlan coursePlan = new CoursePlan(this.getApplicationContext(), subscriptionManager);

        final Preference classPreference = findPreference("className");
        coursePlan.readClassName();

        classPreference.setSummary(coursePlan.className);
        classPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(SettingsActivity.this);
                builderSingle.setTitle("Klasse Auswählen");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        SettingsActivity.this.getApplicationContext(),
                        R.layout.class_chooser);
                arrayAdapter.add("5");
                arrayAdapter.add("6");
                arrayAdapter.add("7");
                arrayAdapter.add("8");
                arrayAdapter.add("9");
                arrayAdapter.add("10");
                arrayAdapter.add("11");
                arrayAdapter.add("12");

                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                coursePlan.className = arrayAdapter.getItem(which);
                                coursePlan.saveClassName();
                                classPreference.setSummary(coursePlan.className);
                            }
                        });

                builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        coursePlan.className = arrayAdapter.getItem(which);
                        coursePlan.saveClassName();
                        classPreference.setSummary(coursePlan.className);
                    }
                });
                builderSingle.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderSingle.show();
                return true;
            }
        });

        findPreference("timetable_refresh").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new TimetableFetchTask(SettingsActivity.this, coursePlan, null).execute();
                return true;
            }
        });
        findPreference("developer")
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://quexten.com/about/"));
                                startActivity(intent);
                                return true;
                            }
                        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }

}
