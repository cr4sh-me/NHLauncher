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
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.SpecialButtonsRecycler.NHLSpecialAdapter;
import com.cr4sh.nhlauncher.SpecialButtonsRecycler.NHLSpecialItem;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.ArrayList;
import java.util.List;

public class SpecialFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_fragment_layout);

        MyPreferences myPreferences = new MyPreferences(this);

        RecyclerView specialRecyclerView = findViewById(R.id.special_recycler_view);

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

        GradientDrawable drawable = new GradientDrawable();
        if (myPreferences.isNewButtonStyleActive()) {
            drawable.setColor(Color.parseColor(myPreferences.color50()));
            drawable.setCornerRadius(60);
        } else {
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
        }

        NHLSpecialAdapter nhlSpecialAdapter = new NHLSpecialAdapter();
        NHLSpecialItem specialItem = new NHLSpecialItem("WPS ATTACK", "USE ONESHOT SCRIPT TO PERFORM WPS ATTACKS", "kali_fern_wifi_cracker");
        NHLSpecialItem specialItem2 = new NHLSpecialItem("BLUETOOTH TOOLKIT", "PERFORM VARIOUS BLUETOOTH ATTACKS", "kali_spooftooph");

        List<NHLSpecialItem> newItemList = new ArrayList<>();
        newItemList.add(specialItem);
        newItemList.add(specialItem2);

        nhlSpecialAdapter.updateData(newItemList);
        specialRecyclerView.setAdapter(nhlSpecialAdapter);

//        customButton1.setBackground(drawable);
////        customButton2.setBackground(drawable);
//        customButton3.setBackground(drawable);
//
//
//        customButton1.setOnClickListener(v -> {
//            VibrationUtil.vibrate(this, 10);
//            Intent intent = new Intent(this, WPSAttack.class);
//            startActivity(intent);
//        });

//        customButton2.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MDKDeauth.class);
//            startActivity(intent);
//        });

//        customButton3.setOnClickListener(v -> {
//            VibrationUtil.vibrate(this, 10);
//            ToastUtils.showCustomToast(this, "Coming soon...");
////            Intent intent = new Intent(this, BluetoothAttacks.class);
////            startActivity(intent);
//        });

        cancelButton.setOnClickListener(v -> {
            VibrationUtil.vibrate(this, 10);
            Intent intent = new Intent(SpecialFeaturesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

    }
}
