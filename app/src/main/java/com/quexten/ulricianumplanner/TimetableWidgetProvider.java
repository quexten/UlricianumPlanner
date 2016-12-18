package com.quexten.ulricianumplanner;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * Created by Quexten on 16-Dec-16.
 */

public class TimetableWidgetProvider extends AppWidgetProvider {

    int[] LAYOUT_IDS = {R.layout.timetable_widget_1, R.layout.timetable_widget_2, R.layout.timetable_widget_3, R.layout.timetable_widget_4, R.layout.timetable_widget_5};
    int[] ROOMVIEW_IDS_LEFT = {R.id.roomView_widget_1_left, R.id.roomView_widget_2_left, R.id.roomView_widget_3_left, R.id.roomView_widget_4_left, R.id.roomView_widget_5_left};
    int[] ROOMVIEW_IDS_RIGHT = {R.id.roomView_widget_1_right, R.id.roomView_widget_2_right, R.id.roomView_widget_3_right, R.id.roomView_widget_4_right, R.id.roomView_widget_5_right};
    int[] SUBJECTVIEW_IDS_LEFT = {R.id.subjectView_widget_1_left, R.id.subjectView_widget_2_left, R.id.subjectView_widget_3_left, R.id.subjectView_widget_4_left, R.id.subjectView_widget_5_left};
    int[] SUBJECTVIEW_IDS_RIGHT = {R.id.subjectView_widget_1_right, R.id.subjectView_widget_2_right, R.id.subjectView_widget_3_right, R.id.subjectView_widget_4_right, R.id.subjectView_widget_5_right};

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.timetable_widget_1);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        // See the dimensions and
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get min width and height.
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = options
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        // Obtain appropriate widget and update it.
        appWidgetManager.updateAppWidget(appWidgetId,
                getRemoteViews(context, minWidth, minHeight));

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
                newOptions);
    }

    private RemoteViews getRemoteViews(Context context, int minWidth,
                                       int minHeight) {
        int rows = getCellsForSize(minHeight);

        CoursePlan coursePlan = new CoursePlan(context);
        coursePlan.read();
        coursePlan.readClassName();
        Substitutions substitutions = new Substitutions(context);
        substitutions.readSubstitutions();

        RemoteViews views = new RemoteViews(context.getPackageName(),
                LAYOUT_IDS[rows - 1]);
        for(int i = 0; i < rows; i++) {
            String todayRoom = coursePlan.getCourse(substitutions.getTodayDay(), Hour.fromInt(i)).room;
            String todaySubject = coursePlan.getCourse(substitutions.getTodayDay(), Hour.fromInt(i)).subject;
            String tomorrowRoom = coursePlan.getCourse(substitutions.getTomorrowDay(), Hour.fromInt(i)).room;
            String tomorrowSubject = coursePlan.getCourse(substitutions.getTomorrowDay(), Hour.fromInt(i)).subject;

            todayRoom = todayRoom.isEmpty() ? " " : todayRoom;
            todaySubject = todaySubject.isEmpty() ? " " : todaySubject;
            tomorrowRoom = tomorrowRoom.isEmpty() ? " " : tomorrowRoom;
            tomorrowSubject = tomorrowSubject.isEmpty() ? " " : tomorrowSubject;

            views.setTextViewText(ROOMVIEW_IDS_LEFT[i], todayRoom);
            views.setTextViewText(SUBJECTVIEW_IDS_LEFT[i], todaySubject);
            views.setTextViewText(ROOMVIEW_IDS_RIGHT[i], tomorrowRoom);
            views.setTextViewText(SUBJECTVIEW_IDS_RIGHT[i], tomorrowSubject);
        }

        return views;
    }

    private static int getCellsForSize(int size) {
        int n = 2;
        while (60 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }

}