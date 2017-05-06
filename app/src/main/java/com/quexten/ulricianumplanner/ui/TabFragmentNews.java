package com.quexten.ulricianumplanner.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quexten.ulricianumplanner.R;

/**
 * Created by Quexten on 04-Mar-17.
 */

public class TabFragmentNews extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_fragment_news, container, false);
        return view;
    }

}