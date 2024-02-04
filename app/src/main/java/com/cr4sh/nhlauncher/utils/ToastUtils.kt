package com.cr4sh.nhlauncher.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cr4sh.nhlauncher.R;

public class ToastUtils {

    public static void showCustomToast(Context context, String message) {
        Context appContext = context.getApplicationContext();
        ViewGroup root = new LinearLayout(appContext);
        View layout = LayoutInflater.from(appContext).inflate(R.layout.nhl_custom_toast, root, false);

        NHLPreferences NHLPreferences = new NHLPreferences(appContext);

        FrameLayout background = layout.findViewById(R.id.customToastLayout);

        // Create a rounded rectangle shape
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(60); // Adjust the corner radius as needed
        shape.setStroke(8, Color.parseColor(NHLPreferences.color80()));
        shape.setColor(Color.parseColor(NHLPreferences.color20())); // Set your background color

        // Set the background drawable for the FrameLayout
        background.setBackground(shape);

        TextView text = layout.findViewById(R.id.customToastText);
        text.setText(message);
        text.setTextColor(Color.parseColor(NHLPreferences.color80()));

        Toast toast = new Toast(appContext);
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        // Create and show the toast
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
