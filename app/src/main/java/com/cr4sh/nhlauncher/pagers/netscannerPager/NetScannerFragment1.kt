package com.cr4sh.nhlauncher.pagers.netscannerPager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.activities.nmapResults.NmapResultsActivity
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ShellExecuter
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.VibrationUtils
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NetScannerFragment1 : Fragment() {
    private val exe = ShellExecuter()

    private lateinit var messageBox: TextView
    @SuppressLint("SdCardPath")
    private val appScriptsPath = "/data/data/com.offsec.nethunter/scripts"
//    private var scanTime = "10"
    private var nhlPreferences: NHLPreferences? = null
    private var vulnersCMD: String = ""
    private var dnsBruteCMD: String = ""
    private var smbEnumSharesCMD: String = ""
    private var ftpAnonCMD: String = ""
    private var smtpEnumCMD: String = ""
    private var bruteCMD: String = ""
    private var allCMD: String = ""
    private var defaultCMD: String = ""
    private var nhlUtils: NHLUtils? = null
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
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

        val dnsBruteCheckBox = view.findViewById<CheckBox>(R.id.dns_brute)
        val smbEnumSharesCheckBox = view.findViewById<CheckBox>(R.id.smb_enum_shares)
        val ftpAnonCheckBox = view.findViewById<CheckBox>(R.id.ftp_anon)
        val smtpEnumCheckBox = view.findViewById<CheckBox>(R.id.smtp_enum)
        val vulnersCheckBox = view.findViewById<CheckBox>(R.id.vulners)
        val bruteCheckBox = view.findViewById<CheckBox>(R.id.brute)
        val allCheckBox = view.findViewById<CheckBox>(R.id.all)
        val defaultCheckBox = view.findViewById<CheckBox>(R.id.defaultVal)

        val targetIp = view.findViewById<EditText>(R.id.target_ip)
        messageBox = view.findViewById(R.id.messagebox)
//        webView = view.findViewById(R.id.webview)

        vulnersCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            vulnersCMD = if (vulnersCheckBox.isChecked) "vulners," else ""
        }

        dnsBruteCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            dnsBruteCMD = if (dnsBruteCheckBox.isChecked) "dns-brute.domains," else ""
        }

        smbEnumSharesCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            smbEnumSharesCMD = if (smbEnumSharesCheckBox.isChecked) "smb-enum-shares," else ""
        }

        ftpAnonCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            ftpAnonCMD = if (ftpAnonCheckBox.isChecked) "ftp-anon," else ""
        }

        smtpEnumCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            smtpEnumCMD = if (smtpEnumCheckBox.isChecked) "smtp-enum-users," else ""
        }

        bruteCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            bruteCMD = if (bruteCheckBox.isChecked) "snmp-brute," else ""
        }

        allCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            allCMD = if (allCheckBox.isChecked) "hydra," else ""
        }

        defaultCheckBox.setOnClickListener {
            VibrationUtils.vibrate(requireActivity(), 10)
            defaultCMD = if (defaultCheckBox.isChecked) "default," else ""
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
                ))
            selectItemByIndex(3) // select a default item.
            lifecycleOwner = this@NetScannerFragment1
        }


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
                ))
            selectItemByIndex(0) // select a default item.
            lifecycleOwner = this@NetScannerFragment1
        }

        powerSpinnerView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView.dividerColor = Color.parseColor(nhlPreferences!!.color80())
        powerSpinnerView.arrowTint = Color.parseColor(nhlPreferences!!.color80())

        powerSpinnerView2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView2.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView2.dividerColor = Color.parseColor(nhlPreferences!!.color80())
        powerSpinnerView2.arrowTint = Color.parseColor(nhlPreferences!!.color80())

        powerSpinnerView.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView.selectItemByIndex(powerSpinnerView.selectedIndex)
            }

        powerSpinnerView2.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView2.selectItemByIndex(powerSpinnerView2.selectedIndex)
            }

        scanButton.setOnClickListener {
            if(targetIp.text.isEmpty()){
                requireActivity().lifecycleScope.launch {
                    showCustomToast(requireActivity(), "Are you dumb?")
                }
            } else {
//                xsltproc nmapscan.xml -o /sdcard/nmap_$(date '+%Y-%m-%d_%H-%M-%S').html
                requireActivity().lifecycleScope.launch(Dispatchers.Default){
                    runNmap(targetIp.text.toString(), powerSpinnerView.text.toString().substringAfter(": "), powerSpinnerView2.text.toString().substringAfter(": "), scanButton)
                }


            }
        }

        return view
    }

    @SuppressLint("SdCardPath", "SetTextI18n")
    private suspend fun runNmap(targetIp: String, options: String, options2: String, scanButton: Button) {
        try {
            requireActivity().lifecycleScope.launch {
                messageBox.text = "Nmap is running..."
                lockButton(false, "Scanning...", scanButton)
            }

            val nmapScan = withContext(Dispatchers.IO) {
                exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd nmap $targetIp -oX /root/nmapscan.xml $options $options2" +
                        " --script=$vulnersCMD$dnsBruteCMD$smbEnumSharesCMD$ftpAnonCMD$smtpEnumCMD$bruteCMD$allCMD$defaultCMD " +
                        "--stats-every 1s && echo 'NHLSCANDONE' || echo 'NHLSCANERROR'")
            }

            Log.d("NMAPSCAN", nmapScan)

            val resultMessage = when {
                nmapScan.contains("NHLSCANDONE") -> {
                    val ref = withContext(Dispatchers.IO) {
                        exe.RunAsRootOutput("$appScriptsPath/bootkali custom_cmd xsltproc /root/nmapscan.xml -o /sdcard/nmap.html && echo 'NHLDONE'")
                    }
                    if (ref.contains("NHLDONE")) {
                        "Scan success" to true
                    } else {
                        "Broken matrix" to false
                    }
                }
                nmapScan.contains("NHLSCANERROR") -> "Scan failed!" to false
                else -> "Something messed up!" to false
            }

            requireActivity().lifecycleScope.launch {
                messageBox.text = if (resultMessage.second) "Scan completed successfully" else "Run scan to display status"
                lockButton(true, "Scan", scanButton)
            }

            if (resultMessage.second) {
                requireActivity().lifecycleScope.launch {
                    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/nmap.html"
                    val intent = Intent(activity, NmapResultsActivity::class.java)
                    intent.putExtra("file_path", filePath)
                    startActivity(intent)
                }
            }

        } catch (e: Exception) {
            Log.e("NMAPSCAN", "Error during Nmap scan: ${e.message}")
            requireActivity().lifecycleScope.launch {
                showCustomToast(requireActivity(), "Something went wrong!")
                messageBox.text = "Run scan to display status"
                lockButton(true, "Scan", scanButton)
            }
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

}