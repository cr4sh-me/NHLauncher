package com.cr4sh.nhlauncher.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

public class ToastUtils {

    public static void showCustomToast(Context context, String message) {
        ViewGroup root = new LinearLayout(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.nhl_custom_toast, root, false);

        MyPreferences myPreferences = new MyPreferences(context);

        FrameLayout background = layout.findViewById(R.id.customToastLayout);

        // Create a rounded rectangle shape
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(60); // Adjust the corner radius as needed
        shape.setStroke(8, Color.parseColor(myPreferences.color80()));
        shape.setColor(Color.parseColor(myPreferences.color20())); // Set your background color

        // Set the background drawable for the FrameLayout
        background.setBackground(shape);

        TextView text = layout.findViewById(R.id.customToastText);
        text.setText(message);
        text.setTextColor(Color.parseColor(myPreferences.color80()));

        // Create and show the toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
