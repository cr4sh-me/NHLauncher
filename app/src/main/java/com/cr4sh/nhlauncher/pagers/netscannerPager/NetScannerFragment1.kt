package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
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
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NetScannerFragment1 : Fragment() {
    private val exe = ShellExecuter()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"

    //    private var scanTime = "10"
    private var nhlPreferences: NHLPreferences? = null
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private lateinit var messageBox: TextView
    private lateinit var recyclerView: FastScrollRecyclerView
    private var completedScan: Int = 0
    private var totalDevices: Int = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var optionsLayout: LinearLayout
    private lateinit var portsSpinner: PowerSpinnerView
    private lateinit var timeSpinner: PowerSpinnerView

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

        progressBar = view.findViewById(R.id.progressBar)

        recyclerView = view.findViewById(R.id.recyclerview)
        optionsLayout = view.findViewById(R.id.options_container)
        portsSpinner = view.findViewById(R.id.ports_spinner)
        timeSpinner = view.findViewById(R.id.time_spinner)
        val scanButton = view.findViewById<Button>(R.id.start_scan)

        portsSpinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem(text = "Top 100: --top-ports 100", null),
                    IconSpinnerItem(text = "Top 250: --top-ports 250", null),
                    IconSpinnerItem(text = "Top 500: --top-ports 500", null),
                    IconSpinnerItem(text = "Top 850: --top-ports 850", null),
                    IconSpinnerItem(text = "Top 1000: --top-ports 1000", null),
                    IconSpinnerItem(text = "All: -p-", null)))
            selectItemByIndex(0) // select a default item.
            lifecycleOwner = this@NetScannerFragment1
        }

        timeSpinner.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("Paranoid (Avoid IDS): -T0", null),
                    IconSpinnerItem("Sneaky (Avoid IDS): -T1", null),
                    IconSpinnerItem("Polite (Slow): -T2", null),
                    IconSpinnerItem("Normal: -T3", null),
                    IconSpinnerItem("Aggressive (Fast): -T4", null),
                    IconSpinnerItem("Insane (Faster): -T5", null),
                )
            )
            selectItemByIndex(3) // select a default item.
            lifecycleOwner = this@NetScannerFragment1
        }

        ColorChanger.setPowerSpinnerColor(portsSpinner)
        ColorChanger.setPowerSpinnerColor(timeSpinner)
        ColorChanger.setButtonColors(scanButton)

        scanButton.setOnClickListener {
            runNmapScan()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private suspend fun getNetworkRange(): String {
        lifecycleScope.launch {
            messageBox.text = "Obtaining network range..."
        }
        return lifecycleScope.async(Dispatchers.IO) {
            try {
                return@async exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd ip route show | awk '/wlan0.*scope/ {print $1}'")
            } catch (e: Exception) {
                e.printStackTrace()
                return@async ""
            }
        }.await()
    }


    private fun isWifiConnected(): Boolean {
        val connectivityManager = mainActivity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    private fun isInternetAvailable(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            // Ping Google's DNS server to check internet connectivity
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    @SuppressLint("SetTextI18n")
    private fun runNmapScan() {
        if(isWifiConnected() && isInternetAvailable()){
            lifecycleScope.launch {
                // Clear recycler
                recyclerView.adapter?.let { adapter ->
                    if (adapter is DeviceAdapter) {
                        adapter.clear()
                    }
                }
                progressBar.isIndeterminate = true
            }
            // TODO: Check if there's a network range
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val range = getNetworkRange()
                    messageBox.text = "Scanning network for devices..."
                    val nmapOutput = exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd nmap -n -sn $range")
                    // Parse the nmapOutput to extract IP, MAC, and vendor details
                    val devices = localDevices(nmapOutput)

                    displayDevices(devices)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            messageBox.text = "Check WiFi or internet connection!"
            progressBar.progress = 0
        }
    }

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
        showOptions(false)
        progressBar.isIndeterminate = false
        totalDevices = devices.size

        lifecycleScope.launch {
            recyclerView.adapter = DeviceAdapter(devices.toMutableList(), this@NetScannerFragment1)

            // Create a list to store all the deferred tasks
            val deferredList = mutableListOf<Deferred<Job>>()

            // Launch a coroutine for scanning each device concurrently
            for (device in devices) {
                // Use launch to run the tasks concurrently
                val deferred = lifecycleScope.async(Dispatchers.IO) {
                    runNmapScoped(device)
                }

                deferredList.add(deferred)
            }

            // Wait for all tasks to complete
            deferredList.forEach { it.join() }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateProgress(completedScans: Int, totalDevices: Int) {
        val progress = (completedScans.toFloat() / totalDevices.toFloat() * 100).toInt()
        lifecycleScope.launch(Dispatchers.Main) {
            if(progress == 100){
                messageBox.text = "Scan done!"
            } else {
                messageBox.text = "Scanning progress: $progress%"
            }
            progressBar.progress = progress
        }
    }



    @SuppressLint("SetTextI18n")
    private fun runNmapScoped(device: DeviceItem): Job {
        return lifecycleScope.launch(Dispatchers.IO) {
            try {
                val command = "$appScriptsPath/bootkali custom_cmd nmap -sV -O ${timeSpinner.text.toString().substringAfter(": ")} ${portsSpinner.text.toString().substringAfter(": ")} -Pn --max-os-tries 1 -n ${device.ip} && echo NHLSCANCOMPLETE"
                Log.d("NMAPCMD", command)
                // Run Nmap command to get detailed information
                val nmapOutput = exe.RunAsRootOutput(command)
                if(nmapOutput.contains("NHLSCANCOMPLETE")){
                    parseNmapOutput(nmapOutput, device)
                    completedScan++
                    updateProgress(completedScan, totalDevices)
                }

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


    private fun showOptions(show: Boolean){
        lifecycleScope.launch {
            if (show){
                recyclerView.visibility = View.GONE
                optionsLayout.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                optionsLayout.visibility = View.GONE
            }
        }
    }


}