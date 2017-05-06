package com.quexten.ulricianumplanner.ui;

/**
 * Created by Quexten on 04-Mar-17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.quexten.ulricianumplanner.ui.TabFragmentNews;
import com.quexten.ulricianumplanner.ui.TabFragmentTimetable;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public PagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabFragmentTimetable tabTimetable = new TabFragmentTimetable();
                return tabTimetable;
            case 1:
                TabFragmentNews tabNews = new TabFragmentNews();
                return tabNews;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}