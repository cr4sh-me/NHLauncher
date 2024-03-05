package com.cr4sh.nhlauncher.pagers.bluetoothPager

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutionException


@Suppress("SameParameterValue")
class BluetoothFragment1 : Fragment() {
    private val exe = ShellExecuter()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"
    var scanTime = "10"
    var nhlPreferences: NHLPreferences? = null
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private var scrollView: ScrollView? = null
    private lateinit var linearContainer: LinearLayout
    private lateinit var binderButton: Button
    private lateinit var servicesButton: Button
    private lateinit var scanButton: Button
    private lateinit var ifaces: PowerSpinnerView
    private lateinit var binderTextView: TextView
    private lateinit var dbusTextView: TextView
    private lateinit var bluetoothTextView: TextView

    //    private lateinit var drawable: Drawable
    private var btSmd: File? = null
    private var bluebinder: File? = null
    private var buttonContainer: LinearLayout? = null
    private var selectedButton: Button? = null
    private lateinit var messageBox: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bt_layout1, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        nhlUtils = mainActivity?.let { NHLUtils(it) }
        btSmd = File("/sys/module/hci_smd/parameters/hcismd_set")
        val chrootPath = "/data/local/nhsystem/kali-arm64"
        bluebinder = File("$chrootPath/usr/sbin/bluebinder")
        scrollView = view.findViewById(R.id.scrollView2)
        buttonContainer = view.findViewById(R.id.buttonContainer)
        linearContainer = view.findViewById(R.id.linearContainer)

        val spinnerTextInfo = view.findViewById<TextView>(R.id.hci_spinner_label)
        spinnerTextInfo.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        messageBox = view.findViewById(R.id.text_msg)

        val binderText1 = view.findViewById<TextView>(R.id.bluebinder)
        val dbusText1 = view.findViewById<TextView>(R.id.dbus)
        val bluetoothText1 = view.findViewById<TextView>(R.id.bluetooth)

        val divider1 = view.findViewById<TextView>(R.id.divider1)
        val divider2 = view.findViewById<TextView>(R.id.divider2)
        val divider3 = view.findViewById<TextView>(R.id.divider3)

        binderTextView = view.findViewById(R.id.binder_text)
        dbusTextView = view.findViewById(R.id.dbus_text)
        bluetoothTextView = view.findViewById(R.id.bluetooth_text)

        binderText1.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        dbusText1.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        bluetoothText1.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        divider1.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        divider2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        divider3.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        binderTextView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        dbusTextView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        bluetoothTextView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        messageBox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        val description = view.findViewById<TextView>(R.id.bt_info2)
        val interfacesText = view.findViewById<TextView>(R.id.interfacesText)
        val spinnerBg1 = view.findViewById<LinearLayout>(R.id.spinnerBg1)
        scanButton = view.findViewById(R.id.scanButton)
        val scanTimeButton = view.findViewById<Button>(R.id.scanTime)
        scanButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        scanButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        scanTimeButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        scanTimeButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        servicesButton = view.findViewById(R.id.servicesButton)
        binderButton = view.findViewById(R.id.bluebinderButton)
        ifaces = view.findViewById(R.id.hci_interface)

        ColorChanger.setPowerSpinnerColor(ifaces)

        ColorChanger.setContainerBackground(spinnerBg1, true)
        description.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        interfacesText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setContainerBackground(linearContainer, false)

        ifaces.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            lifecycleOwner = this@BluetoothFragment1
        }

        lockButton(false, "Please wait...", binderButton)
        lockButton(false, "Please wait...", servicesButton)
        binderStatus
        servicesStatus

        ifaces.setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, newItem ->
            Log.d("SPINNERCLICK", "Selected iface: ${newItem.text}")
            selectedIface = newItem.text.toString()
        }

        binderButton.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    binderAction()
                }
            } catch (e: Exception) {
                // Log exceptions or handle them appropriately
                Log.e("ERROR", "Exception during binderButton click", e)
            }
        }

        servicesButton.setOnClickListener {
            lockButton(false, "Please wait...", servicesButton)
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    // Check if D-Bus is running
                    val isDbusRunning = isDbusRunning()
                    val isBluetoothRunning = isBluetoothRunning()

                    // ASSUMING THAT BLUETOOTH SERVICE CANT RUN WITHOUT DBUS STARTED
                    if (!isDbusRunning && !isBluetoothRunning) {
                        dbusAction(true)
                        bluetoothAction(true)
                    } else if (!isBluetoothRunning) {
                        bluetoothAction(true)
                    } else {
                        dbusAction(false)
                        bluetoothAction(false)
                    }
                }
            } catch (e: Exception) {
                // Log exceptions or handle them appropriately
                Log.e("ERROR", "Exception during bluetoothButton click", e)
            }
        }


        scanButton.setOnClickListener { runBtScan() }
        val dialogUtils = DialogUtils(requireActivity().supportFragmentManager)
        scanTimeButton.setOnClickListener { dialogUtils.openScanTimeDialog(1, this) }
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun createButtons(devices: Array<String>) {
        buttonContainer!!.removeAllViews() // Clear previous buttons
        val buttonCount = 3
        val scrollViewHeight = scrollView!!.height
        val buttonPadding = 15
        for (device in devices) {
            val bluetoothButton = Button(requireActivity())
            val cleanText = device.replace("[\\[\\]]".toRegex(), "")

            // Assume MAC address is always 17 characters
            val bluetoothName = cleanText.substring(18).trim()
            val bluetoothAddress = cleanText.substring(0, 18).trim()
            Log.d("HDH", "$bluetoothName - $bluetoothAddress")
            val ssb = SpannableStringBuilder()
            // Set bold style for BT address
            ssb.append(bluetoothName, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.append("\n")
            ssb.append(bluetoothAddress)
            bluetoothButton.text = ssb
            bluetoothButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

            // Set the background drawable for each button
            ColorChanger.setContainerBackground(bluetoothButton, false, lightTheme = true)

            // Calculate button height dynamically
            val buttonHeight = scrollViewHeight / buttonCount - buttonPadding

            // Set layout parameters for the button
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                buttonHeight
            )
            layoutParams.setMargins(0, buttonPadding / 2, 0, buttonPadding / 2)
            bluetoothButton.layoutParams = layoutParams

            // Add click listener to handle button selection
            bluetoothButton.setOnClickListener { handleButtonClick(bluetoothButton) }
            bluetoothButton.setOnLongClickListener { handleLongButtonClick(bluetoothButton.text.toString()) }

            // Add the button to the container
            buttonContainer!!.addView(bluetoothButton)
        }
    }

    private fun handleLongButtonClick(bluetoothButton: String): Boolean {
        val macAddressPattern = Regex("""([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})""")
        val macAddress = macAddressPattern.find(bluetoothButton)?.value
        nhlUtils?.copyToClipboard(macAddress.toString())
        return true
    }

    private fun handleButtonClick(clickedButton: Button) {
        VibrationUtils.vibrate()
        if (selectedButton != null) {
            selectedButton!!.setTextColor(
                Color.parseColor(
                    nhlPreferences!!.color80()
                )
            )
            ColorChanger.setContainerBackground(selectedButton!!, false, lightTheme = true)
        }

        // If the clicked button is the same as the selected button, deselect it
        if (selectedButton === clickedButton) {
            selectedButton = null
            selectedTarget = null
        } else {
            // Set the text and background color for the clicked button to indicate selection
            clickedButton.setTextColor(Color.parseColor(nhlPreferences!!.color50()))
            ColorChanger.setContainerBackground(clickedButton, false)
            selectedButton = clickedButton
            val macAddressPattern = Regex("""([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})""")
            val macAddress = macAddressPattern.find(selectedButton!!.text.toString())?.value
            selectedTarget = macAddress
        }
    }

    private fun runBtScan() {
        VibrationUtils.vibrate()
        if (selectedIface != "None") {
            lifecycleScope.launch(Dispatchers.Default) {
                try {
                    lifecycleScope.launch {
                        buttonContainer!!.removeAllViews()
                        lockButton(false, "Scanning...", scanButton)
                    }
                    val future1 =
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hciconfig $selectedIface | grep 'UP RUNNING' | cut -f2 -d$'\\t'")
                    if (future1 != "UP RUNNING ") {
                        exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd hciconfig $selectedIface up"))
                    }
                    val future2 =
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hcitool -i $selectedIface scan  --length $scanTime | grep -A 1000 \"Scanning ...\" | awk '/Scanning .../{flag=1;next}/--/{flag=0}flag'\n")
                    Log.d("hcitool", future2)
                    if (future2.isNotEmpty()) {
                        val devicesList =
                            future2.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        Log.d("hcitool", "not empty: " + devicesList.contentToString())
                        lifecycleScope.launch {
                            messageBox.visibility = View.GONE
                            createButtons(devicesList)
                            lockButton(true, "Scan", scanButton)
                        }
                    } else {
                        messageBox.visibility = View.VISIBLE
                        lifecycleScope.launch { lockButton(true, "No devices found!", scanButton) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            requireActivity().lifecycleScope.launch {
                messageBox.visibility = View.VISIBLE
                showCustomToast(requireActivity(), "no selected interface!")
            }
        }
    }

    private val binderStatus: Unit
        get() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isBinderRunningValue = isBinderRunning()

                    lifecycleScope.launch {
                        binderTextView.text = if (isBinderRunningValue) "Running" else "Down"
                        lockButton(
                            true,
                            if (isBinderRunningValue) "Stop Bluebinder" else "Start Bluebinder",
                            binderButton
                        )
                    }

                    // Run loadIfaces on the background thread
                    try {
                        loadIfaces()
                    } catch (e: Exception) {
                        // Log exceptions
                        throw RuntimeException(e)
                    }
                } catch (e: Exception) {
                    // Log exceptions
                    throw RuntimeException(e)
                }
            }
        }


    private val servicesStatus: Unit
        get() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isDbusRunning = isDbusRunning()
                    Log.d("DBUSSTATUS", isDbusRunning.toString())
                    val isBluetoothRunning = isBluetoothRunning()
                    Log.d("BTSTATUS", isBluetoothRunning.toString())

                    lifecycleScope.launch {
                        dbusTextView.text = if (isDbusRunning) "Running" else "Down"
                        bluetoothTextView.text = if (isBluetoothRunning) "Running" else "Down"
                    }

                    if (isDbusRunning && isBluetoothRunning) {
                        lifecycleScope.launch {
                            lockButton(true, "Stop BT services", servicesButton)
                        }
                    } else if (!isDbusRunning && !isBluetoothRunning) {
                        lifecycleScope.launch {
                            lockButton(true, "Start BT services", servicesButton)
                        }
                    } else {
                        lifecycleScope.launch {
                            lockButton(true, "Start BT services", servicesButton)
                        }
                    }
                } catch (e: Exception) {
                    // Log exceptions
                    throw RuntimeException(e)
                }
            }
        }

//    private val dbusStatus: Unit
//        get() {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val isDbusRunning = isDbusRunning()
//                    Log.d("DBUSSTATUS", isDbusRunning.toString())
//                    lifecycleScope.launch {
//                        lockButton(
//                            true,
//                            if (isDbusRunning) "Stop Dbus Service" else "Start Dbus Service",
//                            dbusButton
//                        )
//                    }
//                } catch (e: Exception) {
//                    // Log exceptions
//                    throw RuntimeException(e)
//                }
//            }
//        }

    @Throws(ExecutionException::class, InterruptedException::class)
    suspend fun isBinderRunning(): Boolean {
        return try {
            val outputHCI = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1")
            }
            val binderStatusCmd = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("pidof bluebinder")
            }
            return if (!btSmd!!.exists()) {
                binderStatusCmd.isNotEmpty()
            } else {
                outputHCI.contains("hci0")
            }
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during isBinderRunning", e)
            false
        }
    }


    @Throws(ExecutionException::class, InterruptedException::class)
    suspend fun isBluetoothRunning(): Boolean {
        return try {
            val btStatusCmd = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd service bluetooth status | grep bluetooth")
            }
            Log.d("BTISRUNNING", btStatusCmd)
            btStatusCmd == "bluetooth is running."
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during isBtServicesRunning", e)
            false
        }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    suspend fun isDbusRunning(): Boolean {
        Log.d("DBUSISRUNNING", "START")
        return try {
            val dbusStatusCmd = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd service dbus status | grep dbus")
            }
            Log.d("DBUSISRUNNING", dbusStatusCmd)
            dbusStatusCmd == "dbus is running."
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during isBtServicesRunning", e)
            false
        }
    }

    @SuppressLint("SetTextI18n")
    @Throws(ExecutionException::class, InterruptedException::class)
    private suspend fun binderAction() {
        lockButton(false, "Please wait...", binderButton)
        if (bluebinder!!.exists()) {
            try {

                val isRunningBefore = isBinderRunning()

                // Check if binder is running
                if (!isRunningBefore) {
                    startBinder()
                } else {
                    // Stop bluebinder process in the background
                    stopBinder()
                }

                // Schedule periodic updates using coroutine delay
                while (true) {
                    delay(1000) // Adjust the delay duration if needed

                    try {
                        val isRunningAfterAction = isBinderRunning()
                        if (isRunningBefore != isRunningAfterAction) {
                            // Update button text on the UI thread
                            lifecycleScope.launch {
                                binderStatus
                            }
                            break
                        }
                    } catch (e: Exception) {
                        // Log exceptions
                        Log.e("ERROR", "Exception during periodic update", e)
                    }
                }
            } catch (e: Exception) {
                // Log exceptions
                Log.e("ERROR", "Exception during binderAction", e)
            }
        } else {
            requireActivity().lifecycleScope.launch {
                showCustomToast(
                    requireActivity(),
                    "Bluebinder is not installed. Launch setup first..."
                )
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @Throws(ExecutionException::class, InterruptedException::class)
    private suspend fun bluetoothAction(enable: Boolean) {
        try {
            // Check if BT Services are running


            if (enable) {
                // Start BT Services
//                startBinder()
                startBluetooth()
            } else {
                // Stop BT Services
                stopBluetooth()
            }

            while (true) {
                delay(1000) // Adjust the delay duration if needed

                try {
                    val isRunningAfterAction = isBluetoothRunning()
                    Log.d(
                        "BTACTION",
                        "enable: $enable == isBtServicesRunning $isRunningAfterAction"
                    )
                    if (enable == isRunningAfterAction) {
                        // Update button text on the UI thread
                        lifecycleScope.launch {
                            servicesStatus
                        }
                        break
                    }
                } catch (e: Exception) {
                    // Log exceptions
                    Log.e("ERROR", "Exception during periodic update", e)
                }
            }
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during btServicesAction", e)
        }
    }

    @SuppressLint("SetTextI18n")
    @Throws(ExecutionException::class, InterruptedException::class)
    private suspend fun dbusAction(enable: Boolean) {
        try {

            if (enable) {
                // Start Dbus
                startDbus()
            } else {
                // Stop Dbus
                stopDbus()
            }


            // Schedule periodic updates using coroutine delay
            while (true) {
                delay(1000) // Adjust the delay duration if needed

                try {
                    val isRunningAfterAction = isDbusRunning()
                    Log.d("DBUSACTION", "enable: $enable == isDbusRunning $isRunningAfterAction")
                    if (enable == isRunningAfterAction) {
                        // Update button text on the UI thread
                        lifecycleScope.launch {
                            servicesStatus
                        }
                        break
                    }
                } catch (e: Exception) {
                    // Log exceptions
                    Log.e("ERROR", "Exception during periodic update", e)
                }
            }
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during btServicesAction", e)
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun startBinder() {
        lifecycleScope.launch {
            binderTextView.text = "Starting..."
        }
        try {
            withContext(Dispatchers.IO) {
                if (btSmd!!.exists()) {
                    // Start binder with Bluetooth service manipulation
                    exe.RunAsRoot(arrayOf("svc bluetooth disable"))
                    exe.RunAsRoot(arrayOf("echo 0 > $btSmd"))
                    exe.RunAsRoot(arrayOf("echo 1 > $btSmd"))
                    exe.RunAsRoot(arrayOf("svc bluetooth enable"))
                } else {
                    // Disable Bluetooth service and launch bluebinder
                    exe.RunAsRoot(arrayOf("svc bluetooth disable"))
                    nhlUtils?.runCmd("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during startBinder", e)
        }
    }


    @SuppressLint("SetTextI18n")
    private suspend fun stopBinder() {
        lifecycleScope.launch {
            buttonContainer!!.removeAllViews() // Clear previous buttons
            binderTextView.text = "Stopping..."
        }
        try {
            withContext(Dispatchers.IO) {
                if (btSmd!!.exists()) {
                    // Stop binder
                    exe.RunAsRoot(arrayOf("echo 0 > $btSmd"))
                } else {
                    // Stop binder and enable Bluetooth service
                    exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd pkill bluebinder;exit"))
                    exe.RunAsRoot(arrayOf("svc bluetooth enable"))
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during stopBinder", e)
        }
    }


    @SuppressLint("SetTextI18n")
    private suspend fun startBluetooth() {
        lifecycleScope.launch {
            bluetoothTextView.text = "Starting..."
        }
        try {
            withContext(Dispatchers.IO) {
                // Start BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service bluetooth start"))
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during startBtServices", e)
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun startDbus() {
        lifecycleScope.launch {
            dbusTextView.text = "Starting..."
        }
        try {
            withContext(Dispatchers.IO) {
                // Start BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service dbus start"))
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during startBtServices", e)
        }
    }


    @SuppressLint("SetTextI18n")
    private suspend fun stopBluetooth() {
        lifecycleScope.launch {
            bluetoothTextView.text = "Stopping..."
        }
        try {
            withContext(Dispatchers.IO) {
                // Stop BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service bluetooth stop"))
            }
            Log.d("BT", "Stop operation completed")
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during stopBtServices", e)
        }
    }


    @SuppressLint("SetTextI18n")
    private suspend fun stopDbus() {
        lifecycleScope.launch {
            dbusTextView.text = "Stopping..."
        }
        try {
            withContext(Dispatchers.IO) {
                // Stop BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service dbus stop"))
            }
            Log.d("DBUS", "Stop operation completed")
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during stopBtServices", e)
        }
    }

    private fun lockButton(enable: Boolean, binderButtonText: String, choosenButton: Button?) {
        lifecycleScope.launch {
            choosenButton!!.isEnabled = enable
            choosenButton.text = binderButtonText
            if (enable) {
                choosenButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
                choosenButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
            } else {
                choosenButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color80()))
                choosenButton.setTextColor(Color.parseColor(nhlPreferences!!.color50()))
            }
        }
    }

    private suspend fun loadIfaces() {
        try {
            val outputHCI = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1")
            }

            if (outputHCI.isEmpty()) {
                lifecycleScope.launch {
                    ifaces.apply {
                        setItems(
                            arrayListOf(IconSpinnerItem(text = "None", icon = null))
                        )
                        selectItemByIndex(0) // select a default item.
                    }
                }
            } else {
                val ifacesArray =
                    outputHCI.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                lifecycleScope.launch {
                    ifaces.apply {
                        setItems(
                            arrayListOf(
                                *ifacesArray.map {
                                    IconSpinnerItem(
                                        it,
                                        iconGravity = Gravity.CENTER,
                                        icon = null
                                    )
                                }.toTypedArray()
                            )
                        )
                        selectItemByIndex(0) // select a default item.
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during loadIfaces", e)
        }
    }

    companion object {
        var selectedIface: String = "None"
        var selectedTarget: String? = null
    }

    @SuppressLint("SetTextI18n")
    fun reloadShit() {
        super.onResume()
        if (binderTextView.text.equals("Down") or binderTextView.text.equals("Running")) {
            lifecycleScope.launch {
                lockButton(false, "Reloading...", binderButton)
                binderStatus
            }
        }
        if (dbusTextView.text.equals("Down") or dbusTextView.text.equals("Running") or bluetoothTextView.text.equals(
                "Down"
            ) or bluetoothTextView.text.equals("Running")
        ) {
            lifecycleScope.launch {
                lockButton(false, "Reloading...", servicesButton)
                servicesStatus
            }
        }
    }
}