package com.cr4sh.nhlauncher.dialogs

import android.annotation.SuppressLint
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
import com.cr4sh.nhlauncher.pagers.bluetoothPager.DeviceItem
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils

class NmapDeviceDialog(private val clickedDevice: DeviceItem) : AppCompatDialogFragment() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.device_dialog, container, false)
//        val mainUtils = NHLUtils(myActivity)
        val nhlPreferences = NHLPreferences(requireActivity())

        val bkg = view.findViewById<LinearLayout>(R.id.bkg)
        val deviceIp = view.findViewById<TextView>(R.id.device_ip)
        val deviceMac = view.findViewById<TextView>(R.id.device_mac)
        val deviceVendor = view.findViewById<TextView>(R.id.device_vendor)
        val deviceOs = view.findViewById<TextView>(R.id.device_os)
        val devicePorts = view.findViewById<TextView>(R.id.device_ports)
        val deviceServices = view.findViewById<TextView>(R.id.device_services)


        // Set the text in the TextViews
        deviceIp.text = "IP Address: ${clickedDevice.ip}"
        deviceMac.text = "MAC Address: ${clickedDevice.mac}"
        deviceVendor.text = "Vendor: ${clickedDevice.vendor}"
        deviceOs.text = "OS: ${clickedDevice.os}"
        devicePorts.text = "Ports: ${clickedDevice.ports}"
        deviceServices.text = "Services: ${clickedDevice.services}"

        val cancelButton = view.findViewById<Button>(R.id.cancel_button)

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))

        ColorChanger.setButtonColors(cancelButton, true)

        cancelButton.setOnClickListener {
            VibrationUtils.vibrate()
            dialog?.cancel()
        }
        return view
    }
}
