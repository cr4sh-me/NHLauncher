package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLPreferences
import java.net.NetworkInterface
import java.net.SocketException

class NetScannerFragment2 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    private var dialogUtils: DialogUtils? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.netscanner_layout2, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        dialogUtils = DialogUtils(requireActivity().supportFragmentManager)

//        val scanButton = view.findViewById<Button>(R.id.scanButton)
//        scanButton.setOnClickListener {
//            scanNetwork()
//        }

        return view
    }

    private fun scanNetwork() {
        val deviceList = mutableListOf<String>()

        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()

            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val addresses = networkInterface.inetAddresses

                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()

                    if (!address.isLoopbackAddress) {
                        val hostAddress = address.hostAddress
                        val macAddress = getMacAddress(networkInterface)

                        if (macAddress.isNotBlank()) {
                            val deviceInfo = "IP: $hostAddress\nMAC: $macAddress\n"
                            deviceList.add(deviceInfo)

                            // Log the device information
                            Log.d("DeviceScanner", deviceInfo)
                        }
                    }
                }
            }

            // Display the device information in your UI, for example, using a RecyclerView
            // adapter.submitList(deviceList)

        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    private fun getMacAddress(networkInterface: NetworkInterface): String {
        try {
            val macBytes = networkInterface.hardwareAddress

            // Check if hardware address is null
            if (macBytes != null) {
                val macBuilder = StringBuilder()

                for (byte in macBytes) {
                    macBuilder.append(String.format("%02X:", byte))
                }

                if (macBuilder.isNotEmpty()) {
                    macBuilder.deleteCharAt(macBuilder.length - 1)
                    return macBuilder.toString()
                }
            }

        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return ""
    }


}
