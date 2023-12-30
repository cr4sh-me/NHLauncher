package com.cr4sh.nhlauncher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SpecialFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_fragment_layout);

        MyPreferences myPreferences = new MyPreferences(this);

        TextView title = findViewById(R.id.textView);
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(myPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(myPreferences.color20()));

        CustomButton customButton1 = findViewById(R.id.button1);
        customButton1.setImageResource(R.drawable.kali_fern_wifi_cracker);
        customButton1.setName("WPS ATTACK");
        customButton1.setNameColor(Color.parseColor(myPreferences.color80()));
        customButton1.setDescription("USE ONESHOT SCRIPT TO PERFORM WPS ATTACKS");
        customButton1.setDescriptionColor(Color.parseColor(myPreferences.color80()));

//        CustomButton customButton2 = findViewById(R.id.button2);
//        customButton2.setImageResource(R.drawable.kali_mdk3);
//        customButton2.setName("WIFI DEAUTH");
//        customButton2.setNameColor(Color.parseColor(myPreferences.color80()));
//        customButton2.setDescription("USE MDK4 TO DISCONNECT CLIENTS FROM AP");
//        customButton2.setDescriptionColor(Color.parseColor(myPreferences.color80()));

        CustomButton customButton3 = findViewById(R.id.button3);
        customButton3.setImageResource(R.drawable.kali_tools);
        customButton3.setName("Bluetooth Toolkit");
        customButton3.setNameColor(Color.parseColor(myPreferences.color80()));
        customButton3.setDescription("PERFORM VARIOUS BLUETOOTH ATTACKS");
        customButton3.setDescriptionColor(Color.parseColor(myPreferences.color80()));


        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
        customButton1.setBackground(drawable);
//        customButton2.setBackground(drawable);
        customButton3.setBackground(drawable);


        customButton1.setOnClickListener(v -> {
            Intent intent = new Intent(this, WPSAttack.class);
            startActivity(intent);
        });

//        customButton2.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MDKDeauth.class);
//            startActivity(intent);
//        });

        customButton3.setOnClickListener(v -> {
            Intent intent = new Intent(this, BluetoothAttacks.class);
            startActivity(intent);
        });

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(SpecialFeaturesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }
}
