package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.cr4sh.nhlauncher.R

object ToastUtils {
    @JvmStatic
    fun showCustomToast(context: Context, message: String?) {
        val appContext = context.applicationContext
        val root: ViewGroup = LinearLayout(appContext)
        val layout = LayoutInflater.from(appContext).inflate(R.layout.nhl_custom_toast, root, false)
        val nhlPreferences = NHLPreferences(appContext)
        val background = layout.findViewById<FrameLayout>(R.id.customToastLayout)

        // Create a rounded rectangle shape
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = 60f // Adjust the corner radius as needed
        shape.setStroke(8, Color.parseColor(nhlPreferences.color80()))
        shape.setColor(Color.parseColor(nhlPreferences.color20())) // Set your background color

        // Set the background drawable for the FrameLayout
        background.background = shape
        val text = layout.findViewById<TextView>(R.id.customToastText)
        text.text = message
        text.setTextColor(Color.parseColor(nhlPreferences.color80()))
        val toast = Toast(appContext)
        toast.setGravity(Gravity.BOTTOM, 0, 150)
        // Create and show the toast
        toast.duration = Toast.LENGTH_SHORT
        @Suppress("DEPRECATION")
        toast.view = layout
        toast.show()
    }
}
