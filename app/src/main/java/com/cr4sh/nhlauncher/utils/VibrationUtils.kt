package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object VibrationUtils {
    // Vibrations method
    @JvmStatic
    fun vibrate(context: Context, milliseconds: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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