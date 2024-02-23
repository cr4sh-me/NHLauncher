package com.cr4sh.nhlauncher.activities.wpsAttacks

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.activities.specialFeatures.SpecialFeaturesActivity
import com.cr4sh.nhlauncher.bridge.Bridge.Companion.createExecuteIntent
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.ColorChanger.Companion.activityAnimation
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.LanguageChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// TODO fix wrong button height calculation while keyboard is open
class WPSAttack : LanguageChanger() {
    var customPINCMD = ""
    var delayCMD = ""
    private lateinit var msg2: TextView
    var nhlPreferences: NHLPreferences? = null
    private var isThrottleEnabled = false
    private var pixieCMD = ""
    private var pixieforceCMD = ""
    private var bruteCMD = ""
    private var pbcCMD = ""
    private var selectedButton: Button? = null // Track the currently selected button
    private var wifiManager: WifiManager? = null
    private var buttonContainer: LinearLayout? = null // Container for dynamic buttons
    private var wifiScanReceiver: BroadcastReceiver? = null
    private lateinit var scanButton: Button
    private var exe: ShellExecuter? = null
    private lateinit var textMessage: TextView
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            activityAnimation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityAnimation()

        exe = ShellExecuter()
        setContentView(R.layout.wps_attack_layout)
        msg2 = findViewById(R.id.messageBox2)
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        buttonContainer = findViewById(R.id.buttonContainer)
        nhlPreferences = NHLPreferences(this)
        nhlUtils = mainActivity?.let { NHLUtils(it) }
        val dialogUtils = DialogUtils(supportFragmentManager)
        msg2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        try {
            checkThrottling()
        } catch (e: SettingNotFoundException) {
            throw RuntimeException(e)
        }
        if (nhlPreferences!!.isThrottlingMessageShown and isThrottleEnabled) {
            dialogUtils.openThrottlingDialog()
        }
        if (isThrottleEnabled) {
            setMessage2("Wi-Fi throttling is enabled")
        } else {
            setMessage2("Wi-Fi throttling is disabled")
        }
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences!!.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences!!.color20())
        setFinishOnTouchOutside(false)
        val choiceContainer = findViewById<LinearLayout>(R.id.choiceContainer)
        ColorChanger.setContainerBackground(choiceContainer)
        val title = findViewById<TextView>(R.id.wps_info)
        val description = findViewById<TextView>(R.id.wps_info2)
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        description.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        scanButton = findViewById(R.id.scanButton)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val launchAttackButton = findViewById<Button>(R.id.launchAttack)

        ColorChanger.setButtonColors(launchAttackButton)
        ColorChanger.setButtonColors(cancelButton, true)

        textMessage = findViewById(R.id.text_msg)
        textMessage.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        val checkboxes = arrayOf<CheckBox>(
            findViewById(R.id.pixie),
            findViewById(R.id.pixieforce),
            findViewById(R.id.brute),
            findViewById(R.id.pbc)
        )

        ColorChanger.setupCheckboxesColors(checkboxes)

        val customPinCheckbox = findViewById<Button>(R.id.custompin)
        val delayCheckbox = findViewById<Button>(R.id.delay)

        val pixieDustCheckbox = checkboxes[0]
        val pixieForceCheckbox = checkboxes[1]
        val bruteCheckbox = checkboxes[2]
        val wpsButtonCheckbox = checkboxes[3]

        ColorChanger.setButtonColors(customPinCheckbox)
        ColorChanger.setButtonColors(delayCheckbox)

        pixieDustCheckbox.isChecked = nhlPreferences!!.isPixieDustActive
        pixieCMD = if (pixieDustCheckbox.isChecked) " -K" else ""


        pixieForceCheckbox.isChecked = nhlPreferences!!.isPixieForceActive
        pixieforceCMD = if (pixieForceCheckbox.isChecked) "-F" else ""

        bruteCheckbox.isChecked = nhlPreferences!!.isOnlineBfActive
        bruteCMD = if (bruteCheckbox.isChecked) "-B" else ""

        wpsButtonCheckbox.isChecked = nhlPreferences!!.isWpsButtonActive
        pbcCMD = if (wpsButtonCheckbox.isChecked) "--pbc" else ""

        pixieDustCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            pixieCMD = if (pixieDustCheckbox.isChecked) "-K" else ""
            savePixieDust(pixieDustCheckbox.isChecked)
        }
        pixieForceCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            pixieforceCMD = if (pixieForceCheckbox.isChecked) "-F" else ""
            savePixieForce(pixieForceCheckbox.isChecked)
        }
        bruteCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            bruteCMD = if (bruteCheckbox.isChecked) "-B" else ""
            saveOnlineBf(bruteCheckbox.isChecked)
        }
        customPinCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            dialogUtils.openWpsCustomSetting(1, this@WPSAttack)
        }
        delayCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            dialogUtils.openWpsCustomSetting(2, this@WPSAttack)
        }
        wpsButtonCheckbox.setOnClickListener {
            VibrationUtils.vibrate()
            pbcCMD = if (wpsButtonCheckbox.isChecked) {
                "--pbc"
            } else ""
            saveWpsButton(wpsButtonCheckbox.isChecked)
        }
        enableScanButton(true)
        scanButton.setOnClickListener {
            VibrationUtils.vibrate()
            try {
                checkThrottling()
            } catch (e: SettingNotFoundException) {
                throw RuntimeException(e)
            }
            // Check for location permission before initiating the scan
            if (checkLocationPermission()) {
                if (!wifiManager!!.isWifiEnabled) {
                    enableScanButton(false)
                    setMessage("Enabling WiFi...")
                    enableWifi()
                    lifecycleScope.launch {
                        while (isActive) {
                            delay(3000)
                            performWifiScan()
                        }
                    }
                } else {
                    performWifiScan()
                }
            } else {
                requestLocationPermission()
            }
        }

        // Initialize cancel button
        cancelButton.setOnClickListener {
            VibrationUtils.vibrate()
            val intent = Intent(this@WPSAttack, SpecialFeaturesActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        msg2.setOnClickListener {
            VibrationUtils.vibrate()
            if (isThrottleEnabled) {
                dialogUtils.openThrottlingDialog()
            }
        }

        // Register BroadcastReceiver
        wifiScanReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    handleScanResults()
                }
            }
        }
        launchAttackButton.setOnClickListener {
            VibrationUtils.vibrate()
            if (selectedButton == null) {
                showCustomToast(this, "No target selected!")
            } else {
//                wifiManager!!.disconnect() // disconnect from active ap to prevent issues
                val bssid =
                    extractBSSID(selectedButton!!.text.toString()) // Extract SSID from button text
                Log.d(
                    "LogShit",
                    "cd /root/OneShot && python3 oneshot.py -b $bssid -i wlan0 $pixieCMD $pixieforceCMD $bruteCMD $customPINCMD $delayCMD $pbcCMD"
                )
                runCmd(
                    "cd /root/OneShot && python3 oneshot.py -b $bssid -i wlan0 $pixieCMD $pixieforceCMD $bruteCMD $customPINCMD $delayCMD $pbcCMD"
                )
            }
        }


        // Register BroadcastReceiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        // Set the default message
        setMessage("Ready to scan")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleScanResults() {
        // Retrieve the scan results
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val results = wifiManager!!.scanResults
            if (results.isNotEmpty()) {
                textMessage.visibility = View.GONE
                Log.d("ResultsScan", "results found : $results")
                // Check if there are any WPS networks
                var hasWpsNetworks = false
                for (result in results) {
                    if (result.capabilities != null && result.capabilities.contains("WPS")) {
                        hasWpsNetworks = true
                        break // Break out of the loop since we found at least one WPS network
                    }
                }
                if (hasWpsNetworks) {
                    // There is at least one WPS network in the results
                    createButtons(results)
                    setMessage("Ready to scan")
                } else {
                    // Handle the case where there are no WPS networks found
                    buttonContainer!!.removeAllViews()
                    setMessage("No WPS networks found!")
                }
            } else {
                // Handle the case where there are no Wi-Fi networks found
                Log.d("ResultsScan", "results not found : $results")
                buttonContainer!!.removeAllViews()
                setMessage("No WiFi networks found!")
                textMessage.visibility = View.VISIBLE
            }
            enableScanButton(true)
        } catch (e: Exception) {
            showCustomToast(this, "E: " + e.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    private fun createButtons(results: List<ScanResult>) {
        buttonContainer!!.removeAllViews() // Clear previous buttons
        val buttonCount = 4
        val scrollView = findViewById<ScrollView>(R.id.scrollView2)
        val scrollViewHeight = scrollView.height
        val buttonPadding = 15


        // Sort the results by signal strength in descending order
        for (result in results.sortedByDescending { it.level }) {

            Log.d("LevelShit", "lvl:" + result.level)

            if (result.capabilities != null && result.capabilities.contains("WPS")) {
                val wifiButton = Button(this)

                // Create a SpannableStringBuilder to apply styles to different parts of the text
                val ssb = SpannableStringBuilder()

                // Set bold style for SSID
                val ssidText = result.wifiSsid
                val ssidRes = ssidText.toString().replace("\"", "")
                ssb.append(ssidRes, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.append("\n")
                ssb.append(result.BSSID)
                ssb.append("\n")
                ssb.append(result.level.toString()).append(" dBm")
                wifiButton.text = ssb
                wifiButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

                // Set the background drawable for each button
                val drawable = GradientDrawable()
                drawable.cornerRadius = 60f
                drawable.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
                wifiButton.background = drawable

                // Calculate button height dynamically
                val buttonHeight = scrollViewHeight / buttonCount - buttonPadding

                // Set layout parameters for the button
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    buttonHeight
                )
                layoutParams.setMargins(0, buttonPadding / 2, 0, buttonPadding / 2)
                wifiButton.layoutParams = layoutParams

                // Add click listener to handle button selection
                wifiButton.setOnClickListener { handleButtonClick(wifiButton) }
                wifiButton.setOnLongClickListener { handleLongButtonClick(result.BSSID) }

                // Add the button to the container
                buttonContainer!!.addView(wifiButton)
            }
        }
    }

    private fun handleLongButtonClick(bssid: String): Boolean {
        nhlUtils?.copyToClipboard(bssid)
        return true
    }

    private fun handleButtonClick(clickedButton: Button) {
        VibrationUtils.vibrate()
        if (selectedButton != null) {
            selectedButton!!.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
            // Change the background drawable for the previously selected button
            val drawable = GradientDrawable()
            drawable.cornerRadius = 60f
            drawable.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
            selectedButton!!.background = drawable
        }

        // If the clicked button is the same as the selected button, deselect it
        if (selectedButton === clickedButton) {
            selectedButton = null
        } else {
            // Set the text and background color for the clicked button to indicate selection
            clickedButton.setTextColor(Color.parseColor(nhlPreferences!!.color50()))
            val selectedDrawable = GradientDrawable()
            selectedDrawable.cornerRadius = 60f
            selectedDrawable.setStroke(8, Color.parseColor(nhlPreferences!!.color50()))
            clickedButton.background = selectedDrawable
            selectedButton = clickedButton
        }
    }

    private fun checkLocationPermission(): Boolean {
        // Check if the ACCESS_FINE_LOCATION permission is granted
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        // Request the ACCESS_FINE_LOCATION permission if not granted
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private val isLocationEnabled: Boolean
        get() {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    private fun performWifiScan() {
        if (isLocationEnabled) {
            // Start the Wi-Fi scan
            try {
                val success = wifiManager!!.startScan()
                if (!success && isThrottleEnabled) {
                    enableScanButton(false)
                    buttonContainer!!.removeAllViews()
                    setMessage("Scan limit reached, re-enabling WiFi...")
                    resetWifi()
                } else {
                    enableScanButton(false)
                    buttonContainer!!.removeAllViews()
                    setMessage("Scanning...")
                }
            } catch (e: Exception) {
                buttonContainer!!.removeAllViews()
                showCustomToast(this, "E: " + e.message)
            }
        } else {
            buttonContainer!!.removeAllViews()
            setMessage("Please enable location services first!")
            textMessage.visibility = View.VISIBLE
        }
    }

    private fun enableScanButton(enabled: Boolean) {
        scanButton.isEnabled = enabled
        if (enabled) {
            scanButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
            scanButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        } else {
            textMessage.visibility = View.VISIBLE
            scanButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
            scanButton.setTextColor(Color.parseColor(nhlPreferences!!.color50()))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, show a message or handle accordingly
                buttonContainer!!.removeAllViews()
                setMessage("Location permission denied. Cannot scan for networks.")
                textMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun setMessage(message: String) {
        scanButton.text = message
    }

    private fun setMessage2(message: String) {
        msg2.text = message
    }

    @Throws(SettingNotFoundException::class)
    private fun checkThrottling() {
        isThrottleEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifiManager!!.isScanThrottleEnabled
        } else {
            //based on https://stackoverflow.com/a/61147099 answer
            val enabledInt = Settings.Global.getInt(
                this.contentResolver,
                "wifi_scan_throttle_enabled"
            )
            enabledInt != 1
        }
    }

    private fun runCmd(cmd: String?) {
        @SuppressLint("SdCardPath") val intent =
            createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd!!)
        startActivity(intent)
    }

    private fun enableWifi() {
        lifecycleScope.launch(Dispatchers.IO) {
            exe?.RunAsRoot(arrayOf("svc wifi enable"))
        }
    }

    private fun resetWifi() {
        lifecycleScope.launch(Dispatchers.IO) {
            exe?.RunAsRoot(arrayOf("svc wifi disable"))
            delay(3000)
            exe?.RunAsRoot(arrayOf("svc wifi enable"))
            delay(3000)
            lifecycleScope.launch {
                enableScanButton(true)
                setMessage("Scan")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(wifiScanReceiver)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private fun extractBSSID(buttonText: String): String {
            val lines =
                buttonText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return lines[1].trim { it <= ' ' }
        }
    }

    private fun savePixieDust(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isPixieDustActive", active)
        editor.apply()
    }

    private fun savePixieForce(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isPixieForceActive", active)
        editor.apply()
    }

    private fun saveOnlineBf(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isOnlineBfActive", active)
        editor.apply()
    }

    private fun saveWpsButton(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isWpsButtonActive", active)
        editor.apply()
    }


}