package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Build.VERSION
import androidx.core.content.ContextCompat
import com.google.android.material.R
import java.util.Locale


// This class read, store and return SharedPreferences
class NHLPreferences(
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
                R.color.material_dynamic_primary90
            )
            // Convert the integer color to hexadecimal
            String.format("#%06X", 0xFFFFFF and myColor)
        } else {
            customColorsPrefs.getString("color80", "#e94b3c")
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
            customColorsPrefs.getString("color50", "#4a4a4c")
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
            customColorsPrefs.getString("color20", "#2d2926")
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
        return customColorsPrefs.getBoolean(
            "dynamicThemeBool",
            VERSION.SDK_INT >= Build.VERSION_CODES.S
        ) // Enable dynamic colors at first launch if A12+
    }

    fun advancedThemeBool(): Boolean {
        return customColorsPrefs.getBoolean(
            "advancedThemeBool",
            VERSION.SDK_INT < Build.VERSION_CODES.S // Enable advanced colors at first launch if lower than A12
        )
    }

    fun language(): String? {
        val languageShit = Locale.getDefault().language
        val languageShit2 = if (languageShit == "pl") {
            "DESCRIPTION_PL"
        } else {
            "DESCRIPTION_EN"
        }
        return nhlPrefs.getString("language", languageShit2)
    }

    fun languageLocale(): String {
        return nhlPrefs.getString(
            "languageLocale",
            if (Locale.getDefault().language == "pl") "pl" else "en"
        ) ?: "en"
    }

    fun sortingMode(): String? {
        return nhlPrefs.getString("sortingMode", null)
    }

    val isSetupCompleted: Boolean get() = setupPrefs.getBoolean("isSetupCompleted", false)
    val isThrottlingMessageShown: Boolean
        get() = !setupPrefs.getBoolean(
            "isThrottlingMessageShown",
            false
        )

    fun vibrationOn(): Boolean {
        return nhlPrefs.getBoolean("vibrationsOn", false)
    }

    val isNewButtonStyleActive: Boolean
        get() = nhlPrefs.getBoolean("isNewButtonStyleActive", false)
    val recyclerMainHeight: Int
        get() = nhlPrefs.getInt("recyclerHeight", 0)
    val isButtonOverlayActive: Boolean
        get() = nhlPrefs.getBoolean("isButtonOverlayActive", true)
    val isPixieDustActive: Boolean
        get() = nhlPrefs.getBoolean("isPixieDustActive", false)
    val isPixieForceActive: Boolean
        get() = nhlPrefs.getBoolean("isPixieForceActive", false)
    val isOnlineBfActive: Boolean
        get() = nhlPrefs.getBoolean("isOnlineBfActive", false)
    val isWpsButtonActive: Boolean
        get() = nhlPrefs.getBoolean("isWpsButtonActive", false)

    val customButtonsCount: Int
        get() = nhlPrefs.getInt("customButtonsCount", 8)


//    fun getThemeAccentColor(context: Context): Int {
//        val value = TypedValue()
//        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
//        return value.data
//    }


}