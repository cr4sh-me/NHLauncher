@file:Suppress("DEPRECATION")

package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi

object VibrationUtils {
    // Vibrations method
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.S)
    fun vibrate(context: Context, milliseconds: Long) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
//        val vibrator = VibratorManager
        val nhlPreferences = NHLPreferences(context)
        if (nhlPreferences.vibrationOn()) {
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect = VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                } else {
                    vibrator.vibrate(milliseconds)
                }
            }
        }
    }
}