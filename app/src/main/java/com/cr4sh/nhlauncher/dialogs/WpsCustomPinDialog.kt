package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.wpsAttacks.WPSAttack
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class WpsCustomPinDialog(private val wpsAttack: WPSAttack) : AppCompatDialogFragment() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.wps_custom_pin_dialog, container, false)

        val nhlPreferences = NHLPreferences(requireActivity())
        assert(arguments != null)
        val option = requireArguments().getInt("option")
        isCancelable = false
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val setupButton = view.findViewById<Button>(R.id.setup_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        val customPin = view.findViewById<EditText>(R.id.customPin)
        if (option == 1) {
            cancelButton.text = "Don't use custom PIN"
            title.text = "Custom PIN"
            customPin.hint = "Custom PIN"
            if (wpsAttack.customPINCMD.isNotEmpty()) {
                val customPINCMD = wpsAttack.customPINCMD
                customPin.setText(removePrefix(customPINCMD, " -p "))
            }
        } else {
            cancelButton.text = "Don't use custom delay"
            title.text = "Custom delay"
            customPin.hint = "Custom delay"
            if (wpsAttack.delayCMD.isNotEmpty()) {
                val delayCMD = wpsAttack.delayCMD
                customPin.setText(removePrefix(delayCMD, " -d "))
            }
        }

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))

        ColorChanger.setEditTextColor(customPin)
        ColorChanger.setButtonColors(setupButton)
        ColorChanger.setButtonColors(cancelButton, true)

        setupButton.setOnClickListener {
            vibrate(requireActivity(), 10)
            if (customPin.text.toString().isEmpty()) {
                showCustomToast(requireActivity(), "Empty input!")
            } else {
                dialog?.cancel()
                if (option == 1) {
                    wpsAttack.customPINCMD = " -p " + customPin.text.toString()
                } else {
                    wpsAttack.delayCMD = " -d " + customPin.text.toString()
                }
            }
        }
        cancelButton.setOnClickListener {
            vibrate(requireActivity(), 10)
            dialog?.cancel()
            if (option == 1) {
                wpsAttack.customPINCMD = ""
            } else {
                wpsAttack.delayCMD = ""
            }
        }
        return view
    }

    private fun removePrefix(input: String, prefix: String): String {
        // Check if the input starts with the specified prefix
        return if (input.startsWith(prefix)) {
            // Remove the prefix from the input
            input.substring(prefix.length)
        } else input
        // If the input doesn't start with the prefix, return the original string
    }
}
