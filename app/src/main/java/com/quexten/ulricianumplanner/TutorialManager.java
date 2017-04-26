package com.quexten.ulricianumplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.View;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Quexten on 27-Jan-17.
 */

public class TutorialManager {

    private static final String ACTIVITY_IDENTIFIER = "com.quexten.ulricianumplanner.MainActivity";
    private static final String TUTORIAL_IDENTIFIER = "com.quexten.ulricianumplanner.Tutorial.completed";

    MainActivity activity;

    public TutorialManager(MainActivity activity) {
        this.activity = activity;
        if(!isDone())
            showTutorial();
    }

    private void showTutorial() {
        //Show how to edit timetable dialog
        showShowcaseView(getTarget(R.id.mon34),
                R.string.tutorial_title_01,
                R.string.tutorial_description_01,
                new SimpleShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        final TutorialEditingFragment fragment = TutorialEditingFragment.newInstance();
                        fragment.onViewCreationRunnable = new Runnable() {
                            @Override
                            public void run() {
                                //Show how to edit teachers
                                showShowcaseView(getTarget(fragment.getView(), R.id.TeacherText),
                                        R.string.tutorial_title_02,
                                        R.string.tutorial_description_02,
                                        new SimpleShowcaseEventListener() {
                                            @Override
                                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                showShowcaseView(getTarget(fragment.getView(), R.id.SubjectSpinner),
                                                        R.string.tutorial_title_03,
                                                        R.string.tutorial_description_03,
                                                        new SimpleShowcaseEventListener() {
                                                            @Override
                                                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                                showShowcaseView(getTarget(fragment.getView(), R.id.RoomText),
                                                                        R.string.tutorial_title_04,
                                                                        R.string.tutorial_description_04,
                                                                        new SimpleShowcaseEventListener() {
                                                                            @Override
                                                                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                                                fragment.dismiss();
                                                                                showShowcaseView(getTarget(R.id.framelayout),
                                                                                        R.string.tutorial_title_05,
                                                                                        R.string.tutorial_description_05,
                                                                                        new SimpleShowcaseEventListener() {
                                                                                            @Override
                                                                                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                                                                TutorialManager.this.setDone();
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        };
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.framelayout, fragment, "Some_tag")
                                .commit();
                    }
                });
    }

    void showShowcaseView(Target target, int title, int text, OnShowcaseEventListener listener) {
        ShowcaseView showcaseView1 = new ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setStyle(R.style.AppTheme)
                .setTarget(target)
                .setContentTitle(title)
                .setContentText(text)
                .blockAllTouches()
                .setShowcaseEventListener(listener)
                .build();
    }

    Target getTarget(final int viewId) {
        return new Target() {
            @Override
            public Point getPoint() {
                return new ViewTarget(activity.findViewById(viewId)).getPoint();
            }
        };
    }

    Target getTarget(final View parent, final int viewId) {
        return new Target() {
            @Override
            public Point getPoint() {
                return new ViewTarget(parent.findViewById(viewId)).getPoint();
            }
        };
    }

    public void setDone() {
        SharedPreferences sharedPref = activity.getApplicationContext().getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TUTORIAL_IDENTIFIER, true);
        editor.commit();
    }

    public boolean isDone() {
        SharedPreferences sharedPref = activity.getApplicationContext().getSharedPreferences(ACTIVITY_IDENTIFIER, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(TUTORIAL_IDENTIFIER, false);
    }
}
