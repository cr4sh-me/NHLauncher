package com.cr4sh.nhlauncher.SettingsPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SettingsActivity extends AppCompatActivity {

    public Button cancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        MyPreferences myPreferences = new MyPreferences(this);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(myPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(myPreferences.color20()));

        TextView title = findViewById(R.id.title);
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
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
        tabs.setBackgroundColor(Color.parseColor(myPreferences.color20()));

        // Set tab text color (unselected and selected)
        tabs.setTabTextColors(
                Color.parseColor(myPreferences.color80()),
                Color.parseColor(myPreferences.color80())
        );

        tabs.setSelectedTabIndicatorColor(Color.parseColor(myPreferences.color80()));


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
