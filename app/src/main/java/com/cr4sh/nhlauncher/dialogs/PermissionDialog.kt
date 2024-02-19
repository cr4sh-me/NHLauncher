package com.cr4sh.nhlauncher.dialogs

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.PermissionUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class PermissionDialog : AppCompatDialogFragment() {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.permissions_dialog, container, false)
        isCancelable = false
        val nhlPreferences = NHLPreferences(requireActivity())
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val text = view.findViewById<TextView>(R.id.text_view1)
        val allowButton = view.findViewById<Button>(R.id.allow_button)
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))
        text.setTextColor(Color.parseColor(nhlPreferences.color80()))

        ColorChanger.setButtonColors(allowButton)

        allowButton.setOnClickListener {
            if (mainActivity != null) {
                vibrate(mainActivity, 10)
            }
            dialog?.cancel()
            val permissionUtils = PermissionUtils((requireActivity() as MainActivity))
            permissionUtils.takePermissions()
        }
        return view
    }
}
