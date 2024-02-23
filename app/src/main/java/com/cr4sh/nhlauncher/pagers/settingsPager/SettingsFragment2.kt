package com.cr4sh.nhlauncher.pagers.settingsPager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils
import com.flask.colorpicker.ColorPickerView
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class SettingsFragment2 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    private var hexColorString: String? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_layout2, container, false)
        nhlPreferences = mainActivity?.let { NHLPreferences(it) }
        val manualBox = view.findViewById<LinearLayout>(R.id.hiddenLayout)
        val advancedMode = view.findViewById<LinearLayout>(R.id.advancedLayout)
        val text2 = view.findViewById<TextView>(R.id.text_second)
        val colorPickerView = view.findViewById<ColorPickerView>(R.id.colorPickerView)
        val alphaLayout = view.findViewById<RelativeLayout>(R.id.alphaLayout)
        val alphaTileView1 = view.findViewById<ImageView>(R.id.alphaTileView1)
        val alphaTileView2 = view.findViewById<ImageView>(R.id.alphaTileView2)
        val alphaTileView3 = view.findViewById<ImageView>(R.id.alphaTileView3)
        val alphaLayoutAdv = view.findViewById<RelativeLayout>(R.id.alphaLayoutAdv)
        val alphaTileViewAdv1 = view.findViewById<ImageView>(R.id.alphaTileViewAdv1)
        val alphaTileViewAdv2 = view.findViewById<ImageView>(R.id.alphaTileViewAdv2)
        val alphaTileViewAdv3 = view.findViewById<ImageView>(R.id.alphaTileViewAdv3)
        val hexColorValue1 = view.findViewById<Button>(R.id.customHexColor1)
        val hexColorValue2 = view.findViewById<Button>(R.id.customHexColor2)
        val hexColorValue3 = view.findViewById<Button>(R.id.customHexColor3)
        val hexColorValue = view.findViewById<EditText>(R.id.customHexColor)
        val applyColors = view.findViewById<Button>(R.id.apply_custom_colors)
        hexColorString = nhlPreferences!!.color100()
        hexColorValue.setText(hexColorString)

        val checkboxes = arrayOf<CheckBox>(
            view.findViewById(R.id.dynamic_themes_checkbox),
            view.findViewById(R.id.advanced_themes_checkbox)
        )

        ColorChanger.setupCheckboxesColors(checkboxes)

        val dynamicThemes = checkboxes[0]
        val advancedThemes = checkboxes[1]

        // Checkbox set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicThemes.isChecked = nhlPreferences!!.dynamicThemeBool()
        } else {
            dynamicThemes.isEnabled = false
        }
        if (dynamicThemes.isChecked) {
            manualBox.visibility = View.GONE
        } else {
            manualBox.visibility = View.VISIBLE
            colorPickerView.setInitialColor(Color.parseColor(nhlPreferences!!.color100()), true)
        }
        if (nhlPreferences!!.advancedThemeBool()) {
            advancedThemes.isChecked = true
            advancedMode.visibility = View.VISIBLE
            alphaLayout.visibility = View.GONE
            hexColorValue.visibility = View.GONE
            colorPickerView.visibility = View.GONE
            hexColorValue1.text = nhlPreferences!!.color80()
            hexColorValue2.text = nhlPreferences!!.color50()
            hexColorValue3.text = nhlPreferences!!.color20()
            alphaLayoutAdv.setBackgroundColor(
                Color.parseColor(
                    adjustColorBrightness(
                        hexColorValue3.text.toString(),
                        0.5f
                    )
                )
            )
            alphaTileViewAdv1.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
            alphaTileViewAdv2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
            alphaTileViewAdv3.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        } else {
            advancedThemes.isChecked = false
            advancedMode.visibility = View.GONE
            alphaLayout.visibility = View.VISIBLE
            hexColorValue.visibility = View.VISIBLE
            colorPickerView.visibility = View.VISIBLE
            colorPickerView.setInitialColor(Color.parseColor(nhlPreferences!!.color100()), true)
            alphaLayout.setBackgroundColor(
                Color.parseColor(
                    adjustColorBrightness(
                        nhlPreferences!!.color20(), 0.5f
                    )
                )
            )
            alphaTileView1.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
            alphaTileView2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
            alphaTileView3.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        }

        // Apply custom themes
        text2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setEditTextColor(hexColorValue)

        ColorChanger.setButtonColors(hexColorValue1)
        ColorChanger.setButtonColors(hexColorValue2)
        ColorChanger.setButtonColors(hexColorValue3)
        ColorChanger.setButtonColors(applyColors)

        hexColorValue.isEnabled = false
        dynamicThemes.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                manualBox.visibility = View.GONE
            } else {
                manualBox.visibility = View.VISIBLE
                //                colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
            }
        }
        advancedThemes.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                advancedMode.visibility = View.VISIBLE
                alphaLayout.visibility = View.GONE
                hexColorValue.visibility = View.GONE
                colorPickerView.visibility = View.GONE
                hexColorValue1.text = nhlPreferences!!.color80()
                hexColorValue2.text = nhlPreferences!!.color50()
                hexColorValue3.text = nhlPreferences!!.color20()
                alphaLayoutAdv.setBackgroundColor(
                    Color.parseColor(
                        adjustColorBrightness(
                            hexColorValue3.text.toString(),
                            0.5f
                        )
                    )
                )
                alphaTileViewAdv1.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
                alphaTileViewAdv2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
                alphaTileViewAdv3.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
            } else {
                advancedMode.visibility = View.GONE
                alphaLayout.visibility = View.VISIBLE
                hexColorValue.visibility = View.VISIBLE
                colorPickerView.visibility = View.VISIBLE
                colorPickerView.setInitialColor(Color.parseColor(nhlPreferences!!.color100()), true)
                alphaLayout.setBackgroundColor(
                    Color.parseColor(
                        adjustColorBrightness(
                            nhlPreferences!!.color20(), 0.5f
                        )
                    )
                )
                alphaTileView1.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
                alphaTileView2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
                alphaTileView3.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
            }
        }
        hexColorValue1.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            openPickerDialog(hexColorValue1, alphaTileViewAdv1, nhlPreferences!!.color80())
        }
        hexColorValue2.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            openPickerDialog(hexColorValue2, alphaTileViewAdv2, nhlPreferences!!.color50())
        }
        hexColorValue3.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            openPickerDialog(hexColorValue3, alphaTileViewAdv3, nhlPreferences!!.color20())
        }
        colorPickerView.addOnColorChangedListener { selectedColor: Int ->
            hexColorValue.setText(
                ("#" + Integer.toHexString(selectedColor)).uppercase(
                    Locale.getDefault()
                )
            )
            hexColorString =
                ("#" + Integer.toHexString(selectedColor)).uppercase(Locale.getDefault())

//            alphaLayout.setBackgroundColor(Color.parseColor(adjustColorBrightness(nhlPreferences.color20(), 0.5f)));
            alphaTileView1.setBackgroundColor(
                Color.parseColor(
                    adjustColorBrightness(
                        hexColorString,
                        0.8f
                    )
                )
            )
            alphaTileView2.setBackgroundColor(
                Color.parseColor(
                    adjustColorBrightness(
                        hexColorString,
                        0.5f
                    )
                )
            )
            alphaTileView3.setBackgroundColor(
                Color.parseColor(
                    adjustColorBrightness(
                        hexColorString,
                        0.2f
                    )
                )
            )
        }
        applyColors.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            mainActivity?.lifecycleScope?.launch {
                if (hexColorValue1.text.length < 0 || hexColorValue2.text.length < 0 || hexColorValue3.text.length < 0) {
                    showCustomToast(requireActivity(), "Empty color values! Use brain...")
                } else {
                    if (advancedThemes.isChecked) {
                        val color100 = nhlPreferences!!.color100()
                        val color80 = hexColorValue1.text.toString()
                        val color50 = hexColorValue2.text.toString()
                        val color20 = hexColorValue3.text.toString()
                        saveColor80(
                            color100,
                            color80,
                            color50,
                            color20,
                            dynamicThemes.isChecked,
                            advancedThemes.isChecked
                        )
                    } else {
                        val color100 = hexColorString
                        val color80Int = (alphaTileView1.background as ColorDrawable).color
                        val color50Int = (alphaTileView2.background as ColorDrawable).color
                        val color20Int = (alphaTileView3.background as ColorDrawable).color
                        val color80 = String.format("#%06X", 0xFFFFFF and color80Int)
                        val color50 = String.format("#%06X", 0xFFFFFF and color50Int)
                        val color20 = String.format("#%06X", 0xFFFFFF and color20Int)
                        saveColor80(
                            color100,
                            color80,
                            color50,
                            color20,
                            dynamicThemes.isChecked,
                            advancedThemes.isChecked
                        )
                    }
                    mainActivity.recreate() // recreate MainActivity below
                    requireActivity().recreate() // recreate this activity AFTER to prevent closing db for stats
                }
            }
        }
        return view
    }

    private fun adjustColorBrightness(hexColor: String?, factor: Float): String {
        // https://github.com/edelstone/tints-and-shades
        val color = Color.parseColor(hexColor)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val rMath = (r * factor).toDouble()
        val gMath = (g * factor).toDouble()
        val bMath = (b * factor).toDouble()
        return String.format(
            "#%02X%02X%02X",
            rMath.roundToInt(),
            gMath.roundToInt(),
            bMath.roundToInt()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun openPickerDialog(button: Button, alpha: ImageView, colorShade: String?) {
        val dialogUtils = DialogUtils(requireActivity().supportFragmentManager)
        dialogUtils.openNhlColorPickerDialog(button, alpha, colorShade)
    }

    private fun saveColor80(
        color100: String?,
        color80: String,
        color50: String,
        color20: String,
        dynamicThemeBool: Boolean,
        advThemeBool: Boolean
    ) {
        Log.d("SavedColors", "100: $color100 80: $color80 50: $color50 20: $color20")
        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("customColors", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("color100", color100)
        editor.putString("color80", color80)
        editor.putString("color50", color50)
        editor.putString("color20", color20)
        editor.putBoolean("dynamicThemeBool", dynamicThemeBool)
        editor.putBoolean("advancedThemeBool", advThemeBool)
        editor.apply()
    }
}
