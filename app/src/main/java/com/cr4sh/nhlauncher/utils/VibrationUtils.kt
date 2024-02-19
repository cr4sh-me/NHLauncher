@file:Suppress("DEPRECATION")

package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.cr4sh.nhlauncher.activities.MainActivity

class VibrationUtils {

    // Vibrations method

    companion object {
        val mainActivity: MainActivity? by lazy { NHLManager.getInstance().getMainActivity() }

        fun vibrate(milliseconds: Long = 10) {
            val nhlPreferences = mainActivity?.let { NHLPreferences(it) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    mainActivity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                if (nhlPreferences != null) {
                    if (nhlPreferences.vibrationOn()) {
                        if (vibrator.hasVibrator()) {
                            val vibrationEffect = VibrationEffect.createOneShot(
                                milliseconds,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                            vibrator.vibrate(vibrationEffect)
                        }
                    }
                }
            } else {
                val vibrator = mainActivity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(milliseconds)
                }
            }
        }
    }
}