package com.cr4sh.nhlauncher.dialogs

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class ThrottlingDialog : AppCompatDialogFragment() {
    private val mainActivity: MainActivity = NHLManager.instance.mainActivity
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.throttling_dialog, container, false)

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        val nhlPreferences = NHLPreferences(requireActivity())
        isCancelable = false
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val text = view.findViewById<TextView>(R.id.text_view1)
        val setupButton = view.findViewById<Button>(R.id.setup_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        text.setTextColor(Color.parseColor(nhlPreferences.color80()))
        setupButton.setBackgroundColor(Color.parseColor(nhlPreferences.color50()))
        setupButton.setTextColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setBackgroundColor(Color.parseColor(nhlPreferences.color80()))
        cancelButton.setTextColor(Color.parseColor(nhlPreferences.color50()))
        setupButton.setOnClickListener {
            vibrate(mainActivity, 10)
            dialog?.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            startActivity(intent)
            showCustomToast(requireContext(), "Set Wi-Fi scan throttling to off")
            firstSetupCompleted()
        }
        cancelButton.setOnClickListener {
            vibrate(mainActivity, 10)
            dialog?.cancel()
            firstSetupCompleted()
        }
        return view
    }

    private fun firstSetupCompleted() {
        val prefs = requireActivity().getSharedPreferences("setupSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isThrottlingMessageShown", true)
        editor.apply()
    }
}
