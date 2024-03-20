package com.cr4sh.nhlauncher.pagers.bluetoothPager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.launch

class BluetoothFragment5 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    private var nhlUtils: NHLUtils? = null
    val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private var advertiseSingle: String = ""
    private var advertiseRandom: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bt_layout5, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        nhlUtils = mainActivity?.let { NHLUtils(it) }
        val juiceInfo = view.findViewById<TextView>(R.id.juiceInfo)
        val startButton = view.findViewById<Button>(R.id.startButton)
        val spinnerBg1 = view.findViewById<LinearLayout>(R.id.bottomContainer)
        val powerSpinnerView = view.findViewById<PowerSpinnerView>(R.id.applejuice_spinner)
        val intervalText = view.findViewById<TextView>(R.id.interval_count)
        val intervalEditText = view.findViewById<EditText>(R.id.interval_count_edit)

        ColorChanger.setButtonColors(startButton, false)

        juiceInfo.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        intervalText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setEditTextColor(intervalEditText)
        ColorChanger.setPowerSpinnerColor(powerSpinnerView)
        ColorChanger.setContainerBackground(spinnerBg1, true)

        powerSpinnerView.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                if (newIndex == 0) {
                    advertiseRandom = "-r"
                    advertiseSingle = ""
                } else {
                    advertiseRandom = ""
                    advertiseSingle = "-d $newIndex"
                }
            })

        powerSpinnerView.selectItemByIndex(0)

        startButton.setOnClickListener {
            VibrationUtils.vibrate()
            if (intervalEditText.text.isNotEmpty()) {
                if (checkForSelectedInterface()) {
                    nhlUtils?.runCmd("cd /root/AppleJuice && python3 app.py $advertiseSingle $advertiseRandom -i ${intervalEditText.text}")
                }
            } else {
                requireActivity().lifecycleScope.launch {
                    if (mainActivity != null) {
                        ToastUtils.showCustomToast(mainActivity, "Are you dumb?")
                    }
                }
            }
        }

        return view
    }

    private fun checkForSelectedInterface(): Boolean {
        // Check for interface only
        return if (BluetoothFragment1.selectedIface == "None") {
            mainActivity?.lifecycleScope?.launch {
                ToastUtils.showCustomToast(
                    requireActivity(),
                    "No selected interface!"
                )
            }
            false
        } else {
            true
        }
    }
}
