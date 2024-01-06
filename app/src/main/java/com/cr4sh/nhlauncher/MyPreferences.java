package com.cr4sh.nhlauncher;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import java.util.Locale;

// This class read, store and return SharedPreferences
public class MyPreferences {
    private final SharedPreferences nhlPrefs;
    private final SharedPreferences setupPrefs;
    private final SharedPreferences customColorsPrefs;
    private final Context context;

    public MyPreferences(Context context) {
        this.context = context;
        customColorsPrefs = context.getSharedPreferences("customColors", Context.MODE_PRIVATE);
        nhlPrefs = context.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        setupPrefs = context.getSharedPreferences("setupSettings", Context.MODE_PRIVATE);
    }

    public String color80() {

        if (dynamicThemeBool()) {
            int myColor = ContextCompat.getColor(context, com.google.android.material.R.color.material_dynamic_secondary80);
            // Convert the integer color to hexadecimal
            return String.format("#%06X", (0xFFFFFF & myColor));
        } else {
            return customColorsPrefs.getString("color80", "#ADBDCC");
        }
    }

    public String color50() {
        if (dynamicThemeBool()) {
            int myColor = ContextCompat.getColor(context, com.google.android.material.R.color.material_dynamic_secondary50);
            // Convert the integer color to hexadecimal
            return String.format("#%06X", (0xFFFFFF & myColor));
        } else {
            return customColorsPrefs.getString("color50", "#6C7680");
        }
    }

    public String color20() {

        if (dynamicThemeBool()) {
            int myColor = ContextCompat.getColor(context, com.google.android.material.R.color.material_dynamic_secondary20);
            // Convert the integer color to hexadecimal
            return String.format("#%06X", (0xFFFFFF & myColor));
        } else {
            return customColorsPrefs.getString("color20", "#2B2F33");
        }
    }

    public String color100() {
        if (dynamicThemeBool()) {
            int myColor = ContextCompat.getColor(context, com.google.android.material.R.color.material_dynamic_secondary80);
            // Convert the integer color to hexadecimal
            return String.format("#%06X", (0xFFFFFF & myColor));
        } else {
            return customColorsPrefs.getString("color100", "#ADBDCC");
        }
    }

    public Boolean dynamicThemeBool() {
        return customColorsPrefs.getBoolean("dynamicThemeBool", false);
    }

    public Boolean advancedThemeBool() {
        return customColorsPrefs.getBoolean("advancedThemeBool", false);
    }


    public String language() {
        if (!Locale.getDefault().getLanguage().equals("pl")) {
            return nhlPrefs.getString("language", "DESCRIPTION_EN");
        } else {
            return nhlPrefs.getString("language", "DESCRIPTION_PL");
        }
    }

    public String languageLocale() {
        if (!Locale.getDefault().getLanguage().equals("pl")) {
            return nhlPrefs.getString("languageLocale", "EN");
        } else {
            return nhlPrefs.getString("languageLocale", "PL");
        }
    }

    public String sortingMode() {
        return nhlPrefs.getString("sortingMode", null);
    }

    public boolean isSetupCompleted() {
        return setupPrefs.getBoolean("isSetupCompleted", false);
    }

    public boolean isThrottlingMessageShown() {
        return !setupPrefs.getBoolean("isThrottlingMessageShown", false);
    }

    public boolean vibrationOn() {
        return nhlPrefs.getBoolean("vibrationsOn", false);
    }

    public boolean isNewButtonStyleActive() {
        return nhlPrefs.getBoolean("isNewButtonStyleActive", false);
    }

    public int getRecyclerMainHeight(){
        return nhlPrefs.getInt("recyclerHeight", 0);
    }

}