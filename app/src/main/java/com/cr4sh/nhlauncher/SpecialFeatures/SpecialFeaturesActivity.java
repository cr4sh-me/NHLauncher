package com.cr4sh.nhlauncher.SpecialFeatures;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.SpecialButtonsRecycler.NHLSpecialAdapter;
import com.cr4sh.nhlauncher.SpecialButtonsRecycler.NHLSpecialItem;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.ArrayList;
import java.util.List;

public class SpecialFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.cat_appear, R.anim.cat_appear);
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        }

        setContentView(R.layout.special_fragment_layout);

        NHLPreferences NHLPreferences = new NHLPreferences(this);

        RecyclerView specialRecyclerView = findViewById(R.id.special_recycler_view);

        TextView title = findViewById(R.id.textView);
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(NHLPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(NHLPreferences.color20()));

        GradientDrawable drawable = new GradientDrawable();
        if (NHLPreferences.isNewButtonStyleActive()) {
            drawable.setColor(Color.parseColor(NHLPreferences.color50()));
            drawable.setCornerRadius(60);
        } else {
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(NHLPreferences.color80()));
        }

        NHLSpecialAdapter nhlSpecialAdapter = new NHLSpecialAdapter();
        NHLSpecialItem specialItem = new NHLSpecialItem(getResources().getString(R.string.wps_attack), getResources().getString(R.string.wps_desc_short), "kali_fern_wifi_cracker");
        NHLSpecialItem specialItem2 = new NHLSpecialItem(getResources().getString(R.string.bt_toolkit), getResources().getString(R.string.bt_short), "kali_spooftooph");

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
//            VibrationUtils.vibrate(this, 10);
//            Intent intent = new Intent(this, WPSAttack.class);
//            startActivity(intent);
//        });

//        customButton2.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MDKDeauth.class);
//            startActivity(intent);
//        });

//        customButton3.setOnClickListener(v -> {
//            VibrationUtils.vibrate(this, 10);
//            ToastUtils.showCustomToast(this, "Coming soon...");
////            Intent intent = new Intent(this, BluetoothAttacks.class);
////            startActivity(intent);
//        });

        cancelButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);

            Intent intent = new Intent(SpecialFeaturesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle animationBundle = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear  // Exit animation
            ).toBundle();
            startActivity(intent, animationBundle);
            finish();
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.cat_appear, R.anim.cat_appear);
            } else {
                overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
            }
        }
    }
}
