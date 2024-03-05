package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.overrides.FastScrollRecyclerView
import com.cr4sh.nhlauncher.pagers.bluetoothPager.DeviceAdapter
import com.cr4sh.nhlauncher.pagers.bluetoothPager.DeviceItem
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ShellExecuter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class NetScannerFragment1 : Fragment() {
    private val exe = ShellExecuter()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"

    //    private var scanTime = "10"
    private var nhlPreferences: NHLPreferences? = null
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private lateinit var networkRange: String
    private lateinit var messageBox: TextView
    private lateinit var recyclerView: FastScrollRecyclerView
//    private var x: Int = 0
//    var deviceIp: String = "Loading..."
//    var deviceMac: String = "Loading..."
//    var deviceVendor: String = "Loading..."
//    var deviceOs: String = "Loading..."


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.netscanner_layout1, container, false)

        nhlPreferences = mainActivity?.let { NHLPreferences(it) }
        nhlUtils = mainActivity?.let { NHLUtils(it) }
        messageBox = view.findViewById(R.id.text_gateway)

        recyclerView = view.findViewById(R.id.recyclerview)
        val scanButton = view.findViewById<Button>(R.id.start_scan)
        ColorChanger.setButtonColors(scanButton)

        getNetworkRange()

        scanButton.setOnClickListener {
            runNmapScan()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun getNetworkRange(){
        // TODO check if theres wifi connection first
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                networkRange = exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd ip route show | awk '/wlan0.*scope/ {print $1}'")
                messageBox.text = "Your network range is: $networkRange"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun runNmapScan() {
        lifecycleScope.launch {
            // Clear recycler
            recyclerView.adapter?.let { adapter ->
                if (adapter is DeviceAdapter) {
                    adapter.clear()
                }
            }
            messageBox.text = "Scanning network for devices..."
        }
        // TODO: Check if there's a network range
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Run Nmap command to get IP and MAC details
                val nmapOutput = exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd nmap -n -sn $networkRange")

                // Parse the nmapOutput to extract IP, MAC, and vendor details
                val devices = localDevices(nmapOutput)

                displayDevices(devices)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    data class Device(
//        var ip: String = "",
//        var mac: String = "",
//        var vendor: String = "",
//        val openPorts: MutableList<String> = mutableListOf(),
//        val services: MutableList<String> = mutableListOf(),
//        var os: String = "Unknown"
//    )
    private fun localDevices(nmapOutput: String): List<DeviceItem> {
        val result = mutableListOf<DeviceItem>()
        var device: DeviceItem

        val deviceInfoRegex = Regex("Nmap scan report for (\\S+)(?:.*?MAC Address: (\\S+))?(?:.*?\\(([^)]+)\\))?", RegexOption.DOT_MATCHES_ALL)

        deviceInfoRegex.findAll(nmapOutput).forEach { matchResult ->
            val (ip, mac, vendor) = matchResult.destructured
            if (mac.isNotBlank() && vendor.isNotBlank()) {
                device = DeviceItem(ip.trim(), mac.trim(), vendor.trim())
                result.add(device)
            }
        }

        return result
    }

    private fun displayDevices(devices: List<DeviceItem>) {
        val totalDevices = devices.size

        lifecycleScope.launch {
            recyclerView.adapter = DeviceAdapter(devices.toMutableList(), this@NetScannerFragment1)

            // List to keep track of running jobs
            val jobs = mutableListOf<Job>()

            // Launch a coroutine for scanning each device
            for ((index, device) in devices.withIndex()) {
                Log.d("DeviceInfo", "IP: ${device.ip}, MAC: ${device.mac}, Vendor: ${device.vendor}")

                // Update the messageBox to show progress and current scanning IP
                val progressMessage = "Scanning device ${index + 1} of $totalDevices: ${device.ip}"
                messageBox.text = progressMessage

                // Run Nmap for the current device and add the job to the list
                jobs.add(runNmapScoped(device))
            }

            // Wait for all jobs to complete
            jobs.forEach { it.join() }

            // Reset the messageBox after scanning all devices
            messageBox.text = "Scanning completed for all devices"
        }
    }


    @SuppressLint("SetTextI18n")
    private fun runNmapScoped(device: DeviceItem): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Run Nmap command to get detailed information
                val nmapOutput = exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd nmap -sV -O --top-ports 200 -Pn --max-os-tries 1 -n ${device.ip}")

                // Continue with parsing
                parseNmapOutput(nmapOutput, device)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    @SuppressLint("SetTextI18n")
    private fun parseNmapOutput(nmapOutput: String, device: DeviceItem) {
        // Extract host IP address
        val ipAddressRegex = Regex("Nmap scan report for (\\S+)")
        val ipAddressMatch = ipAddressRegex.find(nmapOutput)
        val ipAddress = ipAddressMatch?.groupValues?.get(1) ?: "Unknown"
        println("IP Address: $ipAddress")

        // Extract open ports and their details
        val openPortsRegex = Regex("(\\d+)/tcp\\s+(\\S+)\\s*(\\S+)?\\s*(.*?)\\s*")

        val openPortsMatches = openPortsRegex.findAll(nmapOutput)

        val ports = arrayListOf<String>()
        val services = arrayListOf<String>()

        for (match in openPortsMatches) {
            val port = match.groupValues[1]
            val state = match.groupValues[2]
            val service = match.groupValues[3]
            val version = match.groupValues[4]
            println("Port: $port, State: $state, Service: $service, Version: $version")
            ports.add(port)
            services.addAll(listOf(service))
        }

        // Extract OS details
        val osDetailsRegex = Regex("OS details: (.+)")
        val osDetailsMatch = osDetailsRegex.find(nmapOutput)
        val osDetails = osDetailsMatch?.groupValues?.get(1) ?: "Unknown"
        println("OS Details: $osDetails")

        lifecycleScope.launch {
            // Clear recycler
            recyclerView.adapter?.let { adapter ->
                if (adapter is DeviceAdapter) {
                    adapter.updateDevice(device.ip, device.mac, device.vendor, osDetails, ports, services)
//                    messageBox.text = "${device.ip} scanned!"
                }
            }
        }
    }





}