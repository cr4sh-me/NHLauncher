package com.cr4sh.nhlauncher;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class DynamicTheme extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
