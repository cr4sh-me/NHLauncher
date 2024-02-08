@file:Suppress("DEPRECATION")

package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationUtils {
    // Vibrations method
    @JvmStatic
    fun vibrate(context: Context, milliseconds: Long) {
        val nhlPreferences = NHLPreferences(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            if (nhlPreferences.vibrationOn()) {
                if (vibrator.hasVibrator()) {
                    val vibrationEffect = VibrationEffect.createOneShot(
                            milliseconds,
                            VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                }
            }
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator.hasVibrator()) {
                    vibrator.vibrate(milliseconds)
            }
        }
    }
}