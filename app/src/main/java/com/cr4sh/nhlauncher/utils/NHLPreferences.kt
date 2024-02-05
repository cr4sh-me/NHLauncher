package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.cr4sh.nhlauncher.MainActivity
import com.google.android.material.R
import java.util.Locale

// This class read, store and return SharedPreferences
class NHLPreferences(context: Context) {
    private val nhlPrefs: SharedPreferences
    private val setupPrefs: SharedPreferences
    private val customColorsPrefs: SharedPreferences
    private val context: MainActivity = NHLManager.instance.mainActivity

    init {
//        this.context = mainActivity;
        customColorsPrefs = context.getSharedPreferences("customColors", Context.MODE_PRIVATE)
        nhlPrefs = context.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        setupPrefs = context.getSharedPreferences("setupSettings", Context.MODE_PRIVATE)
    }

    fun color80(): String? {
        return if (dynamicThemeBool()) {
            val myColor = ContextCompat.getColor(
                context,
                R.color.material_dynamic_secondary80
            )
            // Convert the integer color to hexadecimal
            String.format("#%06X", 0xFFFFFF and myColor)
        } else {
            customColorsPrefs.getString("color80", "#ADBDCC")
        }
    }

    fun color50(): String? {
        return if (dynamicThemeBool()) {
            val myColor = ContextCompat.getColor(
                context,
                R.color.material_dynamic_secondary50
            )
            // Convert the integer color to hexadecimal
            String.format("#%06X", 0xFFFFFF and myColor)
        } else {
            customColorsPrefs.getString("color50", "#6C7680")
        }
    }

    fun color20(): String? {
        return if (dynamicThemeBool()) {
            val myColor = ContextCompat.getColor(
                context,
                R.color.material_dynamic_secondary20
            )
            // Convert the integer color to hexadecimal
            String.format("#%06X", 0xFFFFFF and myColor)
        } else {
            customColorsPrefs.getString("color20", "#2B2F33")
        }
    }

    fun color100(): String? {
        return if (dynamicThemeBool()) {
            val myColor = ContextCompat.getColor(
                context,
                R.color.material_dynamic_secondary80
            )
            // Convert the integer color to hexadecimal
            String.format("#%06X", 0xFFFFFF and myColor)
        } else {
            customColorsPrefs.getString("color100", "#ADBDCC")
        }
    }

    fun dynamicThemeBool(): Boolean {
        return customColorsPrefs.getBoolean("dynamicThemeBool", false)
    }

    fun advancedThemeBool(): Boolean {
        return customColorsPrefs.getBoolean("advancedThemeBool", false)
    }

    fun language(): String? {
        return if (Locale.getDefault().language != "pl") {
            nhlPrefs.getString("language", "DESCRIPTION_EN")
        } else {
            nhlPrefs.getString("language", "DESCRIPTION_PL")
        }
    }

    fun languageLocale(): String? {
        return if (Locale.getDefault().language != "pl") {
            nhlPrefs.getString("languageLocale", "EN")
        } else {
            nhlPrefs.getString("languageLocale", "PL")
        }
    }

    fun sortingMode(): String? {
        return nhlPrefs.getString("sortingMode", null)
    }

    val isSetupCompleted: Boolean
        get() = setupPrefs.getBoolean("isSetupCompleted", false)
    val isThrottlingMessageShown: Boolean
        get() = !setupPrefs.getBoolean("isThrottlingMessageShown", false)

    fun vibrationOn(): Boolean {
        return nhlPrefs.getBoolean("vibrationsOn", false)
    }

    val isNewButtonStyleActive: Boolean
        get() = nhlPrefs.getBoolean("isNewButtonStyleActive", false)
    val recyclerMainHeight: Int
        get() = nhlPrefs.getInt("recyclerHeight", 0)
    val isButtonOverlayActive: Boolean
        get() = nhlPrefs.getBoolean("isButtonOverlayActive", false)
}