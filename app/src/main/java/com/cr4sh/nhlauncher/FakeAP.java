package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import com.cr4sh.nhlauncher.bridge.Bridge;

public class FakeAP extends AppCompatActivity {
    private String pixieCMD = "";
    private String pixieforceCMD = "";
    private String bruteCMD = "";
    public String customPINCMD = "";
    public String delayCMD = "";
    private String pbcCMD = "";
    MyPreferences myPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fake_ap_layout);


        myPreferences = new MyPreferences(this);
        DialogUtils dialogUtils = new DialogUtils(getSupportFragmentManager());


        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(myPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(myPreferences.color20()));

        setFinishOnTouchOutside(false);

        LinearLayout choiceContainer = findViewById(R.id.choiceContainer);
        GradientDrawable selectedDrawable = new GradientDrawable();
        selectedDrawable.setCornerRadius(60);
        selectedDrawable.setStroke(8, Color.parseColor(myPreferences.color50()));
        choiceContainer.setBackground(selectedDrawable);

        TextView title = findViewById(R.id.wps_info);
        TextView description = findViewById(R.id.wps_info2);

        title.setTextColor(Color.parseColor(myPreferences.color80()));
        description.setTextColor(Color.parseColor(myPreferences.color80()));

        Button cancelButton = findViewById(R.id.cancel_button);
        Button launchAttackButton = findViewById(R.id.launchAttack);

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        launchAttackButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        launchAttackButton.setTextColor(Color.parseColor(myPreferences.color80()));

        CheckBox pixieDustCheckbox = findViewById(R.id.pixie);
        CheckBox pixieForceCheckbox = findViewById(R.id.pixieforce);
        CheckBox bruteCheckbox = findViewById(R.id.brute);
        Button customPinCheckbox = findViewById(R.id.custompin);
        Button delayCheckbox = findViewById(R.id.delay);
        CheckBox wpsButtonCheckbox = findViewById(R.id.pbc);

        customPinCheckbox.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        customPinCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));

        delayCheckbox.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        delayCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));


        pixieDustCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        pixieForceCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        bruteCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        wpsButtonCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));


        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(pixieDustCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(pixieForceCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(bruteCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(wpsButtonCheckbox, new ColorStateList(states, colors));

        pixieDustCheckbox.setOnClickListener( v -> {
            if (pixieDustCheckbox.isChecked())
                pixieCMD = " -K";
            else
                pixieCMD = "";
        });
        pixieForceCheckbox.setOnClickListener( v -> {
            if (pixieForceCheckbox.isChecked())
                pixieforceCMD = " -F";
            else
                pixieforceCMD = "";
        });
        bruteCheckbox.setOnClickListener( v -> {
            if (bruteCheckbox.isChecked())
                bruteCMD = " -B";
            else
                bruteCMD = "";
        });
//        customPinCheckbox.setOnClickListener(new View.OnClickListener() {
//            @Override
////            public void onClick(View v) {
////                dialogUtils.openWpsCustomSetting(1, MDKDeauth.this);
////            }
//        });
//        delayCheckbox.setOnClickListener(new View.OnClickListener() {
//            @Override
////            public void onClick(View v) {
////                dialogUtils.openWpsCustomSetting(2, MDKDeauth.this);
////            }
//        });

        wpsButtonCheckbox.setOnClickListener( v -> {
            if (wpsButtonCheckbox.isChecked()) {
                pbcCMD = " --pbc";
            }
            else
                pbcCMD = "";
        });


        // Initialize cancel button
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(FakeAP.this, SpecialFeaturesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        launchAttackButton.setOnClickListener(
                v -> {

                        run_cmd("python3 /sdcard/nh_files/modules/oneshot.py -b " +
                                " -i " + "wlan0" + pixieCMD + pixieforceCMD + bruteCMD + customPINCMD + delayCMD + pbcCMD);
                }
        );
    }


    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver to avoid memory leaks
//        unregisterReceiver(wifiScanReceiver);
//        if (countdownHandler != null) {
//            countdownHandler.removeCallbacksAndMessages(null);
//        }
    }
}
