package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.google.android.material.R
import java.util.Locale

// This class read, store and return SharedPreferences
class NHLPreferences(//    private val context: MainActivity = NHLManager.instance.mainActivity
    private val context: Context
) {
    private val nhlPrefs: SharedPreferences =
        context.getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
    private val setupPrefs: SharedPreferences =
        context.getSharedPreferences("setupSettings", Context.MODE_PRIVATE)
    private val customColorsPrefs: SharedPreferences =
        context.getSharedPreferences("customColors", Context.MODE_PRIVATE)

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
        val languageShit = Locale.getDefault().language.uppercase()
        val languageShit2 = "DESCRIPTION_$languageShit"
        return nhlPrefs.getString("language", languageShit2)
    }

    fun languageLocale(): String? {
        return nhlPrefs.getString("languageLocale", Locale.getDefault().language)
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
    val isPixieDustActive: Boolean
        get() = nhlPrefs.getBoolean("isPixieDustActive", false)
    val isPixieForceActive: Boolean
        get() = nhlPrefs.getBoolean("isPixieForceActive", false)
    val isOnlineBfActive: Boolean
        get() = nhlPrefs.getBoolean("isOnlineBfActive", false)
    val isWpsButtonActive: Boolean
        get() = nhlPrefs.getBoolean("isWpsButtonActive", false)
}