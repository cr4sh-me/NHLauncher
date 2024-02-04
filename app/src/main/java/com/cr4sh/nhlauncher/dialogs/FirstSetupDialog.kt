package com.cr4sh.nhlauncher.dialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.MainUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import java.util.Objects

class FirstSetupDialog : AppCompatDialogFragment() {
    private val mainActivity = NHLManager.getInstance().mainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.first_setup_dialog, container, false)
        val mainUtils = MainUtils((requireActivity() as MainActivity))
        val NHLPreferences = NHLPreferences(requireActivity())
        isCancelable = false
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val text = view.findViewById<TextView>(R.id.text_view1)
        val setupButton = view.findViewById<Button>(R.id.setup_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()))
        title.setTextColor(Color.parseColor(NHLPreferences.color80()))
        text.setTextColor(Color.parseColor(NHLPreferences.color80()))
        setupButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()))
        setupButton.setTextColor(Color.parseColor(NHLPreferences.color80()))
        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()))
        setupButton.setOnClickListener {
            vibrate(mainActivity, 10)
            Objects.requireNonNull(dialog).cancel()
            mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit")
            firstSetupCompleted()
        }
        cancelButton.setOnClickListener {
            vibrate(mainActivity, 10)
            Objects.requireNonNull(dialog).cancel()
            firstSetupCompleted()
        }
        return view
    }

    private fun firstSetupCompleted() {
        val prefs = requireActivity().getSharedPreferences("setupSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isSetupCompleted", true)
        editor.apply()
    }
}
