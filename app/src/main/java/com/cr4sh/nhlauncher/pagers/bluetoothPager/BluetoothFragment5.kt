package com.cr4sh.nhlauncher.pagers.bluetoothPager

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
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

        setButtonColors(startButton)

        juiceInfo.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        intervalText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        intervalEditText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        intervalEditText.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        intervalEditText.background.mutate().setTint(Color.parseColor(nhlPreferences!!.color50()))


        powerSpinnerView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView.dividerColor = Color.parseColor(nhlPreferences!!.color80())

        powerSpinnerView.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView.selectItemByIndex(powerSpinnerView.selectedIndex)
            }

        val gd = GradientDrawable()
        gd.setStroke(8, Color.parseColor(nhlPreferences!!.color50())) // Stroke width and color
        gd.cornerRadius = 20f
        spinnerBg1.background = gd

        powerSpinnerView.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                if(newIndex == 0){
                    advertiseRandom = "-r"
                    advertiseSingle = ""
                } else {
                    advertiseRandom = ""
                    advertiseSingle = "-d $newIndex"
                }
            })

        powerSpinnerView.selectItemByIndex(0)

        startButton.setOnClickListener{
            if(intervalEditText.text.isNotEmpty()){
                if(checkForSelectedInterface()){
                    nhlUtils?.runCmd("cd /root/AppleJuice && python3 app.py $advertiseSingle $advertiseRandom -i ${intervalEditText.text}")
                }
            } else {
                requireActivity().lifecycleScope.launch {
                    showToast("Are you dumb?")
                }
            }
        }

        return view
    }

    private fun showToast(message: String) {
        requireActivity().lifecycleScope.launch {
            ToastUtils.showCustomToast(requireActivity(), message)
        }
    }

    private fun checkForSelectedInterface(): Boolean {
        // Check for interface only
        return if (BluetoothFragment1.selectedIface == "None") {
            requireActivity().lifecycleScope.launch {
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

    private fun setContainerBackground(container: LinearLayout) {
        val drawable = GradientDrawable()
        drawable.cornerRadius = 60f
        drawable.setStroke(8, Color.parseColor(nhlPreferences!!.color50()))
        container.background = drawable
    }

    private fun setButtonColors(button: Button) {
        button.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        button.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
    }
}
