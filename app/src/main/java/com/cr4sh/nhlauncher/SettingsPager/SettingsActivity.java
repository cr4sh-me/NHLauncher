package com.cr4sh.nhlauncher.SettingsPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.VibrationUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SettingsActivity extends AppCompatActivity {

    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        setContentView(R.layout.settings_layout);

        NHLPreferences NHLPreferences = new NHLPreferences(this);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(NHLPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(NHLPreferences.color20()));

        TextView title = findViewById(R.id.title);
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        cancelButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle animationBundle = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear  // Exit animation
            ).toBundle();
            startActivity(intent, animationBundle);
            finish();
        });

        ViewPager2 viewPager2 = findViewById(R.id.pager);
        SettingsPager adapter = new SettingsPager(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setOffscreenPageLimit(4);

        TabLayout tabs = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabs, viewPager2, (tab, position) -> {
            // Set tab titles based on position
            switch (position) {
                case 0 -> tab.setText(getResources().getString(R.string.general));
                case 1 -> tab.setText(getResources().getString(R.string.themes_settings));
                case 2 -> tab.setText(getResources().getString(R.string.statistics));
                case 3 -> tab.setText(getResources().getString(R.string.about));
                // Add more cases for additional tabs if needed
            }
        }).attach();

        // Set tab background color
        tabs.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));

        // Set tab text color (unselected and selected)
        tabs.setTabTextColors(
                Color.parseColor(NHLPreferences.color80()),
                Color.parseColor(NHLPreferences.color80())
        );

        tabs.setSelectedTabIndicatorColor(Color.parseColor(NHLPreferences.color80()));


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
