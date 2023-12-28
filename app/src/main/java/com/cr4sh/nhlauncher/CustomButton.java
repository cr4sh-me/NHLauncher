package com.cr4sh.nhlauncher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// CustomButton.java
public class CustomButton extends LinearLayout {

    private LinearLayout buttonView;
    private ImageView buttonIcon;
    private TextView buttonTextName;
    private TextView buttonTextDescription;


    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout for the custom button
        LayoutInflater.from(context).inflate(R.layout.custom_button, this, true);

        // Find views by their IDs
        buttonView = findViewById(R.id.button_view);
        buttonIcon = findViewById(R.id.button_icon);
        buttonTextName = findViewById(R.id.button_name);
        buttonTextDescription = findViewById(R.id.button_description);
    }

    public void setImageResource(int resId) {
        buttonIcon.setImageResource(resId);
    }

    public void setName(String text) {
        buttonTextName.setText(text);
    }

    public void setDescription(String text) {
        buttonTextDescription.setText(text);
    }

    public void setNameColor(int color) {
        buttonTextName.setTextColor(color);
    }

    public void setDescriptionColor(int color) {
        buttonTextDescription.setTextColor(color);
    }

    public void setBackground(Drawable drawable) {
        buttonView.setBackground(drawable);
    }
}
