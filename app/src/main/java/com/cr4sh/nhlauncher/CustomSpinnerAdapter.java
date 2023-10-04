package com.cr4sh.nhlauncher;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

// This class creates our spinner with custom themes and styles
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final List<Integer> imageList;

    public CustomSpinnerAdapter(Context context, List<String> values, List<Integer> imageList) {
        super(context, R.layout.dropdown_items, values);
        this.imageList = imageList;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dropdown_items, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text_view);
        ImageView imageView = convertView.findViewById(R.id.image_view);

        imageView.setImageResource(imageList.get(position));
        textView.setText(getItem(position));

        // set background color and text color for dropdown items and selected item in spinner
//        convertView.setBackgroundColor(Color.parseColor(backgroundColor));
//        textView.setTextColor(Color.parseColor(textColor));

        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.spinner_layout, parent, false);

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView textView = view.findViewById(R.id.text_view);

        imageView.setImageResource(imageList.get(position));
        textView.setText(getItem(position));

        // Set text color of spinner
//        textView.setTextColor(Color.parseColor(textColor));

        return view;
    }
}
