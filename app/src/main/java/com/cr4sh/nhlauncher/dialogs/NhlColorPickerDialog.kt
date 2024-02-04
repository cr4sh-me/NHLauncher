package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import com.flask.colorpicker.ColorPickerView
import java.util.Locale
import java.util.Objects

class NhlColorPickerDialog(
    private val button: Button,
    private val alpha: ImageView,
    private val hexColorShade: String
) : AppCompatDialogFragment() {
    private val mainActivity = NHLManager.getInstance().mainActivity
    private var hexColorString: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.color_picker_dialog, container, false)
        val NHLPreferences = NHLPreferences(requireActivity())
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val colorPickerView = view.findViewById<ColorPickerView>(R.id.color_picker_view)
        val hexColorValue = view.findViewById<EditText>(R.id.customHexColor)
        val applyColors = view.findViewById<Button>(R.id.apply_custom_colors)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()))
        title.setTextColor(Color.parseColor(NHLPreferences.color80()))
        hexColorValue.setTextColor(Color.parseColor(NHLPreferences.color80()))
        hexColorValue.background.mutate().setTint(Color.parseColor(NHLPreferences.color50()))
        applyColors.setBackgroundColor(Color.parseColor(NHLPreferences.color50()))
        applyColors.setTextColor(Color.parseColor(NHLPreferences.color80()))
        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()))
        colorPickerView.setInitialColor(Color.parseColor(hexColorShade), true)
        hexColorValue.setText(button.text)
        colorPickerView.addOnColorChangedListener { selectedColor: Int ->
            hexColorString = ("#" + Integer.toHexString(selectedColor)).uppercase(
                Locale.getDefault()
            )
            hexColorValue.setText(hexColorString)
        }
        applyColors.setOnClickListener { v: View? ->
            vibrate(mainActivity, 10)
            alpha.setBackgroundColor(Color.parseColor(hexColorString))
            button.text = hexColorString
            Objects.requireNonNull(dialog).cancel()
        }
        cancelButton.setOnClickListener { view1: View? ->
            vibrate(mainActivity, 10)
            Objects.requireNonNull(dialog).cancel()
        }
        return view
    }
}
