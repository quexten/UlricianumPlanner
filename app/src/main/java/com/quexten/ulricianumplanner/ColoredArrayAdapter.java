package com.quexten.ulricianumplanner;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Quexten on 23-Apr-17.
 */

public class ColoredArrayAdapter extends ArrayAdapter {

    private int[] colors;

    public ColoredArrayAdapter(Context context, int viewResourceId, String[] strings) {
        super(context, viewResourceId, strings);
        colors = new int[strings.length];
        for(int i = 0; i < colors.length; i++)
            colors[i] = ResourcesCompat.getColor(context.getResources(), R.color.colorTextPrimary, null);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public String getItem(int position) {
        return String.valueOf(super.getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = ((TextView) view);
        textView.setTextColor(colors[position]);
        return view;
    }

    @Override
    public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = ((TextView) view);
        textView.setTextColor(colors[position]);
        return view;
    }

    public void setColor(int position, int color) {
        colors[position] = color;
    }
}
