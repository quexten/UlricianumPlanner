package com.quexten.ulricianumplanner;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Quexten on 27-Jan-17.
 */

public class TutorialEditingFragment extends android.support.v4.app.DialogFragment {

        public Runnable onViewCreationRunnable;

        public static TutorialEditingFragment newInstance() {
            return new TutorialEditingFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.tutorial_edit_dialog, container, false);
            v.setBackgroundColor(Color.WHITE);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstance) {
            super.onActivityCreated(savedInstance);
            if(onViewCreationRunnable != null)
                onViewCreationRunnable.run();
        }
}
