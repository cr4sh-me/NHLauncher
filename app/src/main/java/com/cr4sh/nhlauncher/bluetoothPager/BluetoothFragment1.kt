package com.cr4sh.nhlauncher.bluetoothPager

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.bridge.Bridge.Companion.createExecuteIntent
import com.cr4sh.nhlauncher.overrides.CustomSpinnerAdapter
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutionException


@Suppress("SameParameterValue")
class BluetoothFragment1 : Fragment() {
    private val exe = ShellExecuter()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"

    //    private val executor = NHLManager.getInstance().executorService
    var scanTime = "10"
    var nhlPreferences: NHLPreferences? = null
    private var scrollView: ScrollView? = null
    private var imageList: List<Int>? = null
    private lateinit var mainHandler: Handler
    private lateinit var binderButton: Button
    private lateinit var servicesButton: Button
    private lateinit var scanButton: Button
    private lateinit var ifaces: Spinner
    private var btSmd: File? = null
    private var bluebinder: File? = null
    private var buttonContainer: LinearLayout? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.bt_layout1, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        mainHandler = Handler(Looper.getMainLooper())
        btSmd = File("/sys/module/hci_smd/parameters/hcismd_set")
        val chrootPath = "/data/local/nhsystem/kali-arm64"
        bluebinder = File("$chrootPath/usr/sbin/bluebinder")
        scrollView = view.findViewById(R.id.scrollView2)
        buttonContainer = view.findViewById(R.id.buttonContainer)
        val description = view.findViewById<TextView>(R.id.bt_info2)
        val interfacesText = view.findViewById<TextView>(R.id.interfacesText)
        val servicesText = view.findViewById<TextView>(R.id.servicesText)
        val scanText = view.findViewById<TextView>(R.id.scanText)
        scanText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        scanButton = view.findViewById(R.id.scanButton)
        val scanTimeButton = view.findViewById<Button>(R.id.scanTime)
        scanButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        scanButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        scanTimeButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        scanTimeButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        binderButton = view.findViewById(R.id.bluebinderButton)
        servicesButton = view.findViewById(R.id.btServicesButton)
        ifaces = view.findViewById(R.id.hci_interface)
        val ifacesContainer = view.findViewById<LinearLayout>(R.id.spinnerContainer)
        setContainerBackground(ifacesContainer)
        imageList = listOf(
            R.drawable.kali_wireless_attacks_trans
        )
        description.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        interfacesText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        servicesText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        binderButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        binderButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        servicesButton.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        servicesButton.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        try {
            CoroutineScope(Dispatchers.IO).launch {
                loadIfaces()
            }
        } catch (e: Exception) {
            // Log exceptions or handle them appropriately
            Log.e("ERROR", "Exception during loadIfaces", e)
        }
        binderStatus
        btServicesStatus
        ifaces.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                pos: Int,
                id: Long
            ) {
                selectedIface = parentView.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // TODO document why this method is empty
            }
        }
        binderButton.setOnClickListener {
            try {
                lockButton(false, "Please wait...", binderButton)
                CoroutineScope(Dispatchers.IO).launch {
                    binderAction()
                }
            } catch (e: Exception) {
                // Log exceptions or handle them appropriately
                Log.e("ERROR", "Exception during binderButton click", e)
            }
        }
        servicesButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    lockButton(false, "Please wait...", servicesButton)
                    btServicesAction()
                } catch (e: Exception) {
                    // Log exceptions or handle them appropriately
                    Log.e("ERROR", "Exception during servicesButton click", e)
                }
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
        val buttonCount = 4
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
            val drawable = GradientDrawable()
            drawable.cornerRadius = 60f
            drawable.setStroke(8, Color.parseColor(nhlPreferences!!.color80()))
            bluetoothButton.background = drawable

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

            // Add the button to the container
            buttonContainer!!.addView(bluetoothButton)
        }
    }

    private fun handleButtonClick(clickedButton: Button) {
        if (selectedButton != null) {
            selectedButton!!.setTextColor(
                Color.parseColor(
                    nhlPreferences!!.color80()
                )
            )
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

    private fun runBtScan() {
        if (selectedIface != "None") {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val future1 =
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hciconfig $selectedIface | grep 'UP RUNNING' | cut -f2 -d$'\\t'")
                    if (future1 != "UP RUNNING ") {
                        exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd hciconfig $selectedIface up"))
                    }
                    mainHandler.post {
                        buttonContainer!!.removeAllViews()
                        lockButton(false, "Scanning...", scanButton)
                    }
                    val future2 =
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd hcitool -i $selectedIface scan  --length $scanTime | grep -A 1000 \"Scanning ...\" | awk '/Scanning .../{flag=1;next}/--/{flag=0}flag'\n")
                    Log.d("hcitool", future2)
                    if (future2.isNotEmpty()) {
                        val devicesList =
                            future2.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        Log.d("hcitool", "not empty: " + devicesList.contentToString())
                        mainHandler.post {
                            createButtons(devicesList)
                            lockButton(true, "Scan", scanButton)
                        }
                    } else {
                        mainHandler.post { lockButton(true, "No devices found...", scanButton) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            showCustomToast(requireActivity(), "no selected interface!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val binderStatus: Unit
        get() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isBinderRunningValue = isBinderRunning()

                    withContext(Dispatchers.Main) {
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


    private val btServicesStatus: Unit
        get() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isBtServicesRunning = isBtServicesRunning()
                    withContext(Dispatchers.Main) {
                        lockButton(
                            true,
                            if (isBtServicesRunning) "Stop BT Services" else "Start BT Services",
                            servicesButton
                        )
                    }
                } catch (e: Exception) {
                    // Log exceptions
                    throw RuntimeException(e)
                }
            }
        }

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
    suspend fun isBtServicesRunning(): Boolean {
        return try {
            val dbusStatusCmd = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd service dbus status | grep dbus")
            }
            val btStatusCmd = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd service bluetooth status | grep bluetooth")
            }
            Log.d("DEBSHIT", "$dbusStatusCmd $btStatusCmd")
            dbusStatusCmd == "dbus is running." && btStatusCmd == "bluetooth is running."
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during isBtServicesRunning", e)
            false
        }
    }

    @SuppressLint("SetTextI18n")
    @Throws(ExecutionException::class, InterruptedException::class)
    private suspend fun binderAction() {
        if (bluebinder!!.exists()) {
            try {
                val isRunningBeforeAction = isBinderRunning()

                // Check if binder is running
                if (!isRunningBeforeAction) {
                    // Start bluebinder process in the background
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
                        if (isRunningBeforeAction != isRunningAfterAction) {
                            // Update button text on the UI thread
                            mainHandler.post {
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
            showCustomToast(requireActivity(), "Bluebinder is not installed. Launch setup first...")
        }
    }


    @SuppressLint("SetTextI18n")
    @Throws(ExecutionException::class, InterruptedException::class)
    private suspend fun btServicesAction() {
        try {
            // Check if BT Services are running
            val areServicesRunning = isBtServicesRunning()

            withContext(Dispatchers.IO) {
                if (!areServicesRunning) {
                    // Start BT Services
                    startBtServices()
                } else {
                    // Stop BT Services
                    stopBtServices()
                }
            }

            // Wait for the action to complete
            delay(1000) // Adjust the delay duration if needed

            // Update button text on the UI thread
            mainHandler.post {
                Log.d("DEBUG", "Update on UI thread")
                btServicesStatus
            }
        } catch (e: Exception) {
            // Log exceptions
            Log.e("ERROR", "Exception during btServicesAction", e)
        }
    }


    private suspend fun startBinder() {
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
                    runCmd("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during startBinder", e)
        }
    }


    private suspend fun stopBinder() {
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


    private suspend fun startBtServices() {
        try {
            withContext(Dispatchers.IO) {
                // Start BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service dbus start"))
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service bluetooth start"))
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during startBtServices", e)
        }
    }


    private suspend fun stopBtServices() {
        try {
            withContext(Dispatchers.IO) {
                // Stop BT services
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service bluetooth stop"))
                exe.RunAsRoot(arrayOf("$appScriptsPath/bootkali custom_cmd service dbus stop"))
            }
            Log.d("BT Services", "Stop operation completed")
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during stopBtServices", e)
        }
    }


    private fun lockButton(value: Boolean, binderButtonText: String, choosenButton: Button?) {
        mainHandler.post {
            choosenButton!!.isEnabled = value
            choosenButton.text = binderButtonText
            if (value) {
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
            val hciIfaces = ArrayList<String?>()
            Log.d(
                "Ifaces",
                outputHCI.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray().contentToString()
            )
            if (outputHCI.isEmpty()) {
                withContext(Dispatchers.Main) {
                    hciIfaces.add("None")
                    val customSpinnerAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        hciIfaces,
                        imageList!!,
                        nhlPreferences!!.color20()!!,
                        nhlPreferences!!.color80()!!
                    )
                    ifaces.adapter = customSpinnerAdapter
                }
            } else {
                val ifacesArray =
                    outputHCI.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                withContext(Dispatchers.Main) {
                    val color20 = nhlPreferences?.color20()
                    val color80 = nhlPreferences?.color80()

                    if (color20 != null) {
                        val customSpinnerAdapter = color80?.let {
                            CustomSpinnerAdapter(
                                requireActivity(),
                                ifacesArray.toList(),
                                imageList!!,
                                color20,
                                it // Provide a default color or handle null case
                            )
                        }
                        ifaces.adapter = customSpinnerAdapter
                    } else {
                        // Handle the case when color20 is null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Exception during loadIfaces", e)
        }
    }


    private fun runCmd(cmd: String?) {
        @SuppressLint("SdCardPath") val intent =
            createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd!!)
        requireActivity().startActivity(intent)
    }

//    private fun run_cmd_background(cmd: String?) {
//        @SuppressLint("SdCardPath") val intent =
//            createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd!!, true)
//        requireActivity().startActivity(intent)
//    }

    private fun setContainerBackground(container: LinearLayout) {
        val drawable = GradientDrawable()
        drawable.cornerRadius = 60f
        drawable.setStroke(8, Color.parseColor(nhlPreferences!!.color50()))
        container.background = drawable
    }

    companion object {
        var selectedIface: String? = null
            private set

        private var selectedButtonWeakRef: WeakReference<Button>? = null

        var selectedButton: Button?
            get() = selectedButtonWeakRef?.get()
            set(value) {
                selectedButtonWeakRef = value?.let { WeakReference(it) }
            }
        val selectedTarget: String
            get() = selectedButton!!.text.toString()
    }
}