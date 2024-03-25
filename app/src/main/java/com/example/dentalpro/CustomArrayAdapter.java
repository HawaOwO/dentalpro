package com.example.dentalpro;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<CharSequence> {
    private int[] colors;

    public CustomArrayAdapter(Context context, int resource, CharSequence[] objects, int[] colors) {
        super(context, resource, objects);
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(colors[0]); // Set text color
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setTextColor(Color.WHITE); // Set text color
        //textView.setBackgroundColor(Color.WHITE);
        return textView;
    }
}

