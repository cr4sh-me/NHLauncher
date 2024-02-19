package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.activities.nmapResults.NmapResultsActivity
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class NetScannerFragment1 : Fragment() {
    private val exe = ShellExecuter()

    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"

    //    private var scanTime = "10"
    private var nhlPreferences: NHLPreferences? = null
    private var scriptsCMD: String = ""

    //    private var dnsBruteCMD: String = ""
//    private var smbEnumSharesCMD: String = ""
//    private var ftpAnonCMD: String = ""
//    private var smtpEnumCMD: String = ""
//    private var bruteCMD: String = ""
//    private var allCMD: String = ""
//    private var defaultCMD: String = ""
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private lateinit var mySwitch: SwitchCompat
    private lateinit var mySwitch2: SwitchCompat
    private lateinit var mySwitch3: SwitchCompat
    private var pnCMD = ""
    private var ipv6CMD = ""

//    private lateinit var webView: Cu
//    private var scrollView: ScrollView? = null
//    private lateinit var linearContainer: LinearLayout
//    private lateinit var binderButton: Button
//    private lateinit var servicesButton: Button
//    private lateinit var scanButton: Button
//    private lateinit var ifaces: PowerSpinnerView
//    private lateinit var binderTextView: TextView
//    private lateinit var dbusTextView: TextView
//    private lateinit var bluetoothTextView: TextView
//
//    //    private lateinit var drawable: Drawable
//    private var btSmd: File? = null
//    private var bluebinder: File? = null
//    private var buttonContainer: LinearLayout? = null
//    private var selectedButton: Button? = null
//    private lateinit var messageBox: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.netscanner_layout1, container, false)

        nhlPreferences = NHLPreferences(requireActivity())
        nhlUtils = mainActivity?.let { NHLUtils(it) }

        val scanButton = view.findViewById<Button>(R.id.start_scan)
        val powerSpinnerView = view.findViewById<PowerSpinnerView>(R.id.nmap_modes)
        val powerSpinnerView2 = view.findViewById<PowerSpinnerView>(R.id.nmap_times)

        val checkboxes = arrayOf<CheckBox>(
            view.findViewById(R.id.auth),
            view.findViewById(R.id.broadcast),
            view.findViewById(R.id.default_sc),
            view.findViewById(R.id.discovery),
            view.findViewById(R.id.dos),
            view.findViewById(R.id.exploit),
            view.findViewById(R.id.external),
            view.findViewById(R.id.fuzzer),
            view.findViewById(R.id.intrusive),
            view.findViewById(R.id.malware),
            view.findViewById(R.id.safe),
            view.findViewById(R.id.version),
            view.findViewById(R.id.vuln)
        )

        checkboxes.forEach { checkbox -> checkbox.setOnClickListener { onCheckboxClicked(checkboxes) } }
        ColorChanger.setupCheckboxesColors(checkboxes)

        val description = view.findViewById<TextView>(R.id.bt_info2)
        val textview4 = view.findViewById<TextView>(R.id.textView4)
        val targetIp = view.findViewById<EditText>(R.id.target_ip)
        val modeText = view.findViewById<TextView>(R.id.selected_mode)
        val timeText = view.findViewById<TextView>(R.id.selected_time)
        val bg1 = view.findViewById<LinearLayout>(R.id.spinnerBg1)
        val bg2 = view.findViewById<LinearLayout>(R.id.spinnerBg2)
        mySwitch = view.findViewById(R.id.switchWidget)
        mySwitch2 = view.findViewById(R.id.switchWidget2)
        mySwitch3 = view.findViewById(R.id.switchWidget3)

        ColorChanger.setContainerBackground(bg1, true)
        ColorChanger.setContainerBackground(bg2, true)

        mySwitch2.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            pnCMD = if (b) "-Pn" else ""
        }

        mySwitch3.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            ipv6CMD = if (b) "-6" else ""
        }

        powerSpinnerView2.apply {
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

        ColorChanger.setButtonColors(scanButton, false)

        powerSpinnerView.apply {
            setSpinnerAdapter(IconSpinnerAdapter(this))
            setItems(
                arrayListOf(
                    IconSpinnerItem("Intense Stealth: -sS -A -Pn", null),
                    IconSpinnerItem("TCP SYN: -sS", null),
                    IconSpinnerItem("TCP Connect: -sT", null),
                    IconSpinnerItem("TCP ACK: -sA", null),
                    IconSpinnerItem("UDP Scan: -sU", null),
                    IconSpinnerItem("ALL Ports: -p1-65535", null),
                    IconSpinnerItem("ALL Ports SYN: -sS -p1-65535", null),
                    IconSpinnerItem("ALL Ports Connect: -sT -p1-65535", null),
                    IconSpinnerItem("Top 50 Ports: --top-ports 50", null),
                    IconSpinnerItem("Stealth Version: -sS -sV", null),
                    IconSpinnerItem("OS Detection: -O", null),
                    IconSpinnerItem("OS Detection SYN: -sS -O", null)
                )
            )
            selectItemByIndex(0) // select a default item.
            lifecycleOwner = this@NetScannerFragment1
        }

        ColorChanger.setSwitchColor(mySwitch)
        ColorChanger.setSwitchColor(mySwitch2)
        ColorChanger.setSwitchColor(mySwitch3)

        description.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        textview4.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        modeText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        timeText.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setEditTextColor(targetIp)

        ColorChanger.setPowerSpinnerColor(powerSpinnerView)
        ColorChanger.setPowerSpinnerColor(powerSpinnerView2)

        scanButton.setOnClickListener {
            if (targetIp.text.isEmpty()) {
                mainActivity?.lifecycleScope?.launch {
                    showCustomToast(requireActivity(), "Are you dumb?")
                }
            } else {
//                xsltproc nmapscan.xml -o /sdcard/nmap_$(date '+%Y-%m-%d_%H-%M-%S').html
                mainActivity?.lifecycleScope?.launch(Dispatchers.Default) {
                    runNmap(
                        targetIp.text.toString(),
                        powerSpinnerView.text.toString().substringAfter(": "),
                        powerSpinnerView2.text.toString().substringAfter(": "),
                        scanButton
                    )
                }


            }
        }

        return view
    }

    @SuppressLint("SdCardPath", "SetTextI18n")
    private suspend fun runNmap(
        targetIp: String,
        options: String,
        options2: String,
        scanButton: Button
    ) {
        if (mySwitch.isChecked) {
            try {
                requireActivity().lifecycleScope.launch {
                    lockButton(false, "Scanning...", scanButton)
                }

                // 1. Check and delete log file if exist
                val filePath = Environment.getExternalStorageDirectory().absolutePath + "/nmap.html"
                val file = File(filePath)
                if (file.exists()) {
                    val deleted = file.delete()

                    if (deleted) {
                        println("File deleted successfully")
                    } else {
                        println("Failed to delete the file")
                    }
                } else {
                    println("File does not exist")
                }

                // 2. Run nmap and create new log file
                val nmapScan = withContext(Dispatchers.IO) {
                    exe.RunAsRootOutput(
                        "$appScriptsPath/bootkali custom_cmd nmap $targetIp -oX /root/nmapscan.xml --stylesheet /root/nmap-bootstrap.xsl $options $options2" +
                                " $scriptsCMD $pnCMD $ipv6CMD" +
                                "&& echo 'NHLSCANDONE' || echo 'NHLSCANERROR'"
                    )
                }

                Log.d("NMAPSCAN", nmapScan)

                // Check if nmap didn't throw any errors
                if (nmapScan.contains("NHLSCANDONE")) {
                    val ref = withContext(Dispatchers.IO) {
//                            xsltproc -o scanme.html nmap-bootstrap.xsl scanme.xml
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd xsltproc -o /sdcard/nmap.html /root/nmap-bootstrap.xsl /root/nmapscan.xml && echo 'NHLDONE'")
                    }
                    mainActivity?.lifecycleScope?.launch {
                        lockButton(true, "Scan", scanButton)
                    }
                    Log.d("REF", ref)
                    if (ref.contains("NHLDONE")) {
                        mainActivity?.lifecycleScope?.launch {
                            val intent = Intent(activity, NmapResultsActivity::class.java)
                            intent.putExtra("file_path", filePath)
                            startActivity(intent)
                        }
                    } else {
                        mainActivity?.lifecycleScope?.launch {
                            showCustomToast(requireActivity(), "Something went wrong!")
                        }
                    }
                } else {
                    requireActivity().lifecycleScope.launch {
                        showCustomToast(requireActivity(), "Something went wrong!")
                        lockButton(true, "Scan", scanButton)
                    }
                }
            } catch (e: Exception) {
                mainActivity?.lifecycleScope?.launch {
                    showCustomToast(requireActivity(), "Something went wrong!")
                }
            }
        } else {
            nhlUtils?.runCmd(
                "nmap $targetIp $options $options2 " +
                        "$scriptsCMD $pnCMD $ipv6CMD"
            )
        }
    }


    private fun lockButton(enable: Boolean, binderButtonText: String, choosenButton: Button?) {
        mainActivity?.lifecycleScope?.launch {
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


    private fun onCheckboxClicked(x: Array<CheckBox>) {
        val selectedCheckboxes = x.filter { it.isChecked }

        scriptsCMD = if (selectedCheckboxes.isNotEmpty()) {
            val optionText = selectedCheckboxes.joinToString(",") { it.text.toString() }
            "--script $optionText"
        } else {
            "-sn" // With the -sn option it is possible to run a script scan without a port scan, only host discovery
        }
    }

}