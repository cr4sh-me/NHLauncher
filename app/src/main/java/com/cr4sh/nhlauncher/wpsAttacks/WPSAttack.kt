package com.cr4sh.nhlauncher.wpsAttacks

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils.replace
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.bridge.Bridge.Companion.createExecuteIntent
import com.cr4sh.nhlauncher.specialFeatures.SpecialFeaturesActivity
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WPSAttack : AppCompatActivity() {
    private val executorService = NHLManager.getInstance().executorService
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

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    R.anim.cat_appear,
                    R.anim.cat_appear
                )
            } else {
                overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.cat_appear,
                R.anim.cat_appear
            )
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
        }
        exe = ShellExecuter()
        setContentView(R.layout.wps_attack_layout)
        msg2 = findViewById(R.id.messageBox2)
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        buttonContainer = findViewById(R.id.buttonContainer)
        nhlPreferences = NHLPreferences(this)
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
            setMessage2("Wi-Fi throttling enabled")
        } else {
            setMessage2("Wi-Fi throttling disabled")
        }
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences!!.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences!!.color20())
        setFinishOnTouchOutside(false)
        val choiceContainer = findViewById<LinearLayout>(R.id.choiceContainer)
        val selectedDrawable = GradientDrawable()
        selectedDrawable.cornerRadius = 60f
        selectedDrawable.setStroke(8, Color.parseColor(nhlPreferences!!.color50()))
        choiceContainer.background = selectedDrawable
        val title = findViewById<TextView>(R.id.wps_info)
        val description = findViewById<TextView>(R.id.wps_info2)
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        description.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        scanButton = findViewById(R.id.scanButton)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val launchAttackButton = findViewById<Button>(R.id.launchAttack)
        cancelButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
        cancelButton.setTextColor(Color.parseColor(nhlPreferences!!.color50()))
        launchAttackButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        launchAttackButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        val pixieDustCheckbox = findViewById<CheckBox>(R.id.pixie)
        val pixieForceCheckbox = findViewById<CheckBox>(R.id.pixieforce)
        val bruteCheckbox = findViewById<CheckBox>(R.id.brute)
        val customPinCheckbox = findViewById<Button>(R.id.custompin)
        val delayCheckbox = findViewById<Button>(R.id.delay)
        val wpsButtonCheckbox = findViewById<CheckBox>(R.id.pbc)
        customPinCheckbox.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        customPinCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        delayCheckbox.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        delayCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        pixieDustCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        pixieForceCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        bruteCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        wpsButtonCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        pixieDustCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        pixieForceCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        bruteCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        wpsButtonCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        pixieDustCheckbox.setOnClickListener {
            vibrate(this, 10)
            pixieCMD = if (pixieDustCheckbox.isChecked) " -K" else ""
        }
        pixieForceCheckbox.setOnClickListener {
            vibrate(this, 10)
            pixieforceCMD = if (pixieForceCheckbox.isChecked) " -F" else ""
        }
        bruteCheckbox.setOnClickListener {
            vibrate(this, 10)
            bruteCMD = if (bruteCheckbox.isChecked) " -B" else ""
        }
        customPinCheckbox.setOnClickListener {
            vibrate(this, 10)
            dialogUtils.openWpsCustomSetting(1, this@WPSAttack)
        }
        delayCheckbox.setOnClickListener {
            vibrate(this, 10)
            dialogUtils.openWpsCustomSetting(2, this@WPSAttack)
        }
        wpsButtonCheckbox.setOnClickListener {
            vibrate(this, 10)
            pbcCMD = if (wpsButtonCheckbox.isChecked) {
                " --pbc"
            } else ""
        }
        enableScanButton(true)
        scanButton.setOnClickListener {
            vibrate(this, 10)
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
                    coroutineScope.launch {
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
            vibrate(this, 10)
            val intent = Intent(this@WPSAttack, SpecialFeaturesActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val animationBundle = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.cat_appear,  // Enter animation
                R.anim.cat_disappear // Exit animation
            ).toBundle()
            startActivity(intent, animationBundle)
            finish()
        }
        msg2.setOnClickListener {
            vibrate(this, 10)
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
            vibrate(this, 10)
            if (selectedButton == null) {
                showCustomToast(this, "No target selected!")
            } else {
//                wifiManager!!.disconnect() // disconnect from active ap to prevent issues
                val bssid =
                    extractBSSID(selectedButton!!.text.toString()) // Extract SSID from button text
                runCmd(
                    "cd /root/OneShot && python3 oneshot.py -b " + bssid +
                            " -i " + "wlan0" + pixieCMD + pixieforceCMD + bruteCMD + customPINCMD + delayCMD + pbcCMD
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
        results.sortedBy { it.level }

        for (result in results) {
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

                // Add the button to the container
                buttonContainer!!.addView(wifiButton)
            }
        }
    }

    private fun handleButtonClick(clickedButton: Button) {
        vibrate(this, 10)
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
                    setMessage("Scan limit reached, re-enabling WiFi...")
                    resetWifi()
                    coroutineScope.launch {
                        while (isActive) {
                            delay(5000)
                            performWifiScan()
                        }
                    }
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
        }
    }

    private fun enableScanButton(enabled: Boolean) {
        scanButton.isEnabled = enabled
        if (enabled) {
            scanButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
            scanButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        } else {
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
            createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd!!, false)
        startActivity(intent)
    }

    private fun enableWifi() {
        executorService.submit { exe!!.RunAsRoot(arrayOf("svc wifi enable")) }
    }

    private fun resetWifi() {
        executorService.submit { exe!!.RunAsRoot(arrayOf("svc wifi disable")) }
        coroutineScope.launch {
            while (isActive) {
                delay(3000)
                exe?.RunAsRoot(arrayOf("svc wifi enable"))
            }
        }
//        Handler().postDelayed(
//            { executorService.submit { exe!!. } },
//            3000
//        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(wifiScanReceiver)
        //        NHLManager.getInstance().shutdownExecutorService();
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private fun extractBSSID(buttonText: String): String {
            val lines =
                buttonText.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return lines[1].trim { it <= ' ' }
        }
    }
}