package com.cr4sh.nhlauncher.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.CompoundButtonCompat
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import com.skydoves.powerspinner.PowerSpinnerView

class ColorChanger {
    companion object {

        val nhlPreferences: NHLPreferences by lazy {
            NHLManager.getInstance().getMainActivity()
                ?.let { NHLPreferences(context = it) }!!
        }
        fun setButtonColors(button: Button, cancel: Boolean = false) {
            button.setBackgroundColor(Color.parseColor(if(cancel) nhlPreferences.color80() else nhlPreferences.color50()))
            button.setTextColor(Color.parseColor(if(cancel) nhlPreferences.color50() else nhlPreferences.color80()))
        }

        fun setEditTextColor(editText: EditText){
            editText.setTextColor(Color.parseColor(nhlPreferences.color80()))
            editText.setHintTextColor(Color.parseColor(nhlPreferences.color50()))
            editText.background.mutate().setTint(Color.parseColor(nhlPreferences.color50()))
        }

        fun setContainerBackground(container: View, spinner: Boolean = false, lightTheme: Boolean = false) {
            val drawable = GradientDrawable()
            drawable.cornerRadius = if(spinner) 20f else 60f
            drawable.setStroke(8, Color.parseColor(if(lightTheme) nhlPreferences.color80() else nhlPreferences.color50()))
            container.background = drawable
        }

        fun setupCheckboxesColors(x: Array<CheckBox>) {
            x.forEach { checkbox ->
                CompoundButtonCompat.setButtonTintList(
                    checkbox,
                    ColorStateList.valueOf(Color.parseColor(nhlPreferences.color80()))
                )
                checkbox.setTextColor(Color.parseColor(nhlPreferences.color80()))
            }
        }

        fun setPowerSpinnerColor(v: PowerSpinnerView){

            v.spinnerOutsideTouchListener =
                OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                    v.selectItemByIndex(v.selectedIndex)
            }

            v.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
            v.setTextColor(Color.parseColor(nhlPreferences.color80()))
            v.setHintTextColor(Color.parseColor(nhlPreferences.color50()))
            v.dividerColor = Color.parseColor(nhlPreferences.color80())
            v.arrowTint = Color.parseColor(nhlPreferences.color80())
        }

        fun setSwitchColor(v: SwitchCompat) {
            val thumbColor = Color.parseColor(nhlPreferences.color80())
            val trackColor = Color.parseColor(nhlPreferences.color50())

            v.setTextColor(thumbColor)

            v.thumbDrawable?.let {
                DrawableCompat.setTintList(
                    it, ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(
                            thumbColor,
                            thumbColor
                        )
                    )
                )
            }
            v.trackDrawable?.let {
                DrawableCompat.setTintList(
                    it, ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(
                            trackColor,
                            trackColor
                        )
                    )
                )
            }
        }
    }
}