package com.cr4sh.nhlanucher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.util.Locale;

public class MyPreferences {
    private final SharedPreferences customColorsPrefs;
    private final SharedPreferences customFontsPrefs;
    private final SharedPreferences nhlPrefs;
    private final SharedPreferences setupPrefs;
    private final Context context;
    public MyPreferences(Context context) {
        this.context = context;
        customColorsPrefs = context.getSharedPreferences("customColors", Context.MODE_PRIVATE);
        customFontsPrefs = context.getSharedPreferences("customFonts", Context.MODE_PRIVATE);
        nhlPrefs = context.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        setupPrefs = context.getSharedPreferences("setupSettings", Context.MODE_PRIVATE);
    }

    public String buttonColor() {
        return customColorsPrefs.getString("buttonColor", "#4A4A4C");
    }

    public String nameColor() {
        return customColorsPrefs.getString("nameColor", "#FFFFFF");
    }

    public String descriptionColor() {
        return customColorsPrefs.getString("descriptionColor", "#e94b3c");
    }

    public String strokeColor() {
        return customColorsPrefs.getString("strokeColor", "#4A4A4C");
    }

    public String fontName() {
        return customFontsPrefs.getString("fontName", "roboto_bold");
    }

    public String frameColor() {
        return customColorsPrefs.getString("frameColor", "frame6");
    }

    public String logoIcon() {
        return customColorsPrefs.getString("logoIcon", "nhlauncher");
    }

    public Typeface typeface() {
        String font = fontName();
        return Typeface.createFromAsset(context.getAssets(), "font/" + font + ".ttf");
    }

    public String language() {
        return nhlPrefs.getString("language", "DESCRIPTION_" + Locale.getDefault().getLanguage());
    }

    public String languageLocale() {
        return nhlPrefs.getString("languageLocale", Locale.getDefault().getLanguage());
    }

    public String sortingMode() {
        return nhlPrefs.getString("sortingMode", null);
    }

    public boolean isSetupCompleted() {
        return setupPrefs.getBoolean("isSetupCompleted", false);
    }

    public int splashDuration() { return nhlPrefs.getInt("splashDuration", 800); }

    public boolean animateButtons() { return nhlPrefs.getBoolean("animateButtons", true); }

}
