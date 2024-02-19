package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.pagers.bluetoothPager.BluetoothFragment1.Companion.selectedIface
import com.cr4sh.nhlauncher.pagers.bluetoothPager.BluetoothFragment1.Companion.selectedTarget
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.VibrationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SdpToolDialog : AppCompatDialogFragment() {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"
    val exe = ShellExecuter()

    @SuppressLint("Recycle")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sdptool_dialog, container, false)
        val nhlPreferences = NHLPreferences(requireActivity())
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val title = view.findViewById<TextView>(R.id.dialog_title)
        val copyButton = view.findViewById<Button>(R.id.copy_button)
        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
        val sdpText = view.findViewById<TextView>(R.id.sdp_text)
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences.color80()))

        sdpText.setTextColor(Color.parseColor(nhlPreferences.color80()))

        ColorChanger.setButtonColors(copyButton)
        ColorChanger.setButtonColors(cancelButton, true)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rfcommCmd =
                    exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd sdptool -i $selectedIface browse $selectedTarget | sed '/^\\[/d' | sed '/^Linux/d'")
                Log.d("BTF2", rfcommCmd)
                requireActivity().lifecycleScope.launch {
                    sdpText.text = rfcommCmd.trim()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        copyButton.setOnClickListener {
            mainActivity?.mainUtils?.copyToClipboard(sdpText.text)
        }

        cancelButton.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate(mainActivity, 10)
            }
            dialog?.cancel()
        }
        return view
    }
}