package com.cr4sh.nhlauncher;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

// This class read, store and return SharedPreferences
public class MyPreferences {
    private final SharedPreferences nhlPrefs;
    private final SharedPreferences setupPrefs;
    private SharedPreferences customColorsPrefs;
    private final Context context;

    public MyPreferences(Context context) {
        this.context = context;
        customColorsPrefs = context.getSharedPreferences("customColors", Context.MODE_PRIVATE);
        nhlPrefs = context.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        setupPrefs = context.getSharedPreferences("setupSettings", Context.MODE_PRIVATE);
    }

    public String color80() {
        return customColorsPrefs.getString("color80", "#e60000");
    }
    public String color50() {
        return customColorsPrefs.getString("color50", "#660000");
    }
    public String color20() {
        return customColorsPrefs.getString("color20", "#4c0000");
    }
    public String color100() {return customColorsPrefs.getString("color100", "#FF0000");}
    public Boolean dynamicThemeBool() {return customColorsPrefs.getBoolean("dynamicThemeBool", false);}

//
//    public String nameColor() {
//        return customColorsPrefs.getString("nameColor", "#FFFFFF");
//    }
//
//    public String descriptionColor() {
//        return customColorsPrefs.getString("descriptionColor", "#e94b3c");
//    }
//
//    public String strokeColor() {
//        return customColorsPrefs.getString("strokeColor", "#4A4A4C");
//    }
//
//    public String fontName() {
//        return customFontsPrefs.getString("fontName", "roboto_bold");
//    }
//
//    public String frameColor() {
//        return customColorsPrefs.getString("frameColor", "frame6");
//    }
//
//    public String logoIcon() {
//        return customColorsPrefs.getString("logoIcon", "nhlauncher");
//    }

//    public Typeface typeface() {
//        String font = fontName();
//        return Typeface.createFromAsset(context.getAssets(), "font/" + font + ".ttf");
//    }

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

}
