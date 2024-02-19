package com.cr4sh.nhlauncher.pagers.settingsPager

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.database.DBBackup
import com.cr4sh.nhlauncher.utils.ColorChanger
import com.cr4sh.nhlauncher.utils.LanguageChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.UpdateCheckerUtils
import com.cr4sh.nhlauncher.utils.UpdateCheckerUtils.UpdateCheckResult
import com.cr4sh.nhlauncher.utils.VibrationUtils
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment1 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    var mainUtils: NHLUtils? = null
    val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    private val languageChanger = LanguageChanger()
    private lateinit var updateButton: Button
    private var newSortingModeSetting: String? = null
    private var newLanguageNameSetting: String? = null
    private var newLanguageLocaleSetting: String? = null
    private var buttonsCount: Int? = null
    private lateinit var vibrationsCheckbox: CheckBox
    private lateinit var newButtonsStyle: CheckBox
    private lateinit var overlayCheckbox: CheckBox

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_layout1, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        mainUtils = mainActivity?.let { NHLUtils(it) }

        val checkboxes = arrayOf<CheckBox>(
            view.findViewById(R.id.vibrations_checkbox),
            view.findViewById(R.id.newbuttons_checkbox),
            view.findViewById(R.id.overlay_checkbox)
        )
        ColorChanger.setupCheckboxesColors(checkboxes)

        vibrationsCheckbox = checkboxes[0]
        newButtonsStyle = checkboxes[1]
        overlayCheckbox = checkboxes[2]
        val title = view.findViewById<TextView>(R.id.bt_info2)
        val bkg = view.findViewById<ScrollView>(R.id.custom_theme_dialog_background)
        val runSetup = view.findViewById<Button>(R.id.run_setup)
        val backupDb = view.findViewById<Button>(R.id.db_backup)
        val restoreDb = view.findViewById<Button>(R.id.db_restore)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val seekBar = view.findViewById<SeekBar>(R.id.seekbar)
        val seekBarDesc = view.findViewById<TextView>(R.id.seekbar_description)
        val seekBarValue = view.findViewById<TextView>(R.id.seekbar_value)
        updateButton = view.findViewById(R.id.update_button)
        val checkUpdate = view.findViewById<TextView>(R.id.checkUpdate)
        val spinnerText1 = view.findViewById<TextView>(R.id.language_spinner_label)
        val spinnerText2 = view.findViewById<TextView>(R.id.sorting_spinner_label)
        spinnerText1.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        spinnerText2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        val powerSpinnerView = view.findViewById<PowerSpinnerView>(R.id.language_spinner)
        val powerSpinnerView2 = view.findViewById<PowerSpinnerView>(R.id.sorting_spinner)
        val spinnerBg1 = view.findViewById<LinearLayout>(R.id.spinnerBg1)
        val spinnerBg2 = view.findViewById<LinearLayout>(R.id.spinnerBg2)
        checkUpdate.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setPowerSpinnerColor(powerSpinnerView)
        ColorChanger.setPowerSpinnerColor(powerSpinnerView2)

        seekBarDesc.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        seekBarValue.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        seekBar.progressDrawable.colorFilter = BlendModeColorFilter(
            Color.parseColor(nhlPreferences!!.color80()), BlendMode.SRC_IN
        )
        seekBar.thumb.colorFilter = BlendModeColorFilter(
            Color.parseColor(nhlPreferences!!.color80()), BlendMode.SRC_IN
        )

        bkg.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))

        ColorChanger.setButtonColors(runSetup)
        ColorChanger.setButtonColors(backupDb)
        ColorChanger.setButtonColors(restoreDb)
        ColorChanger.setButtonColors(saveButton)

        vibrationsCheckbox.isChecked = nhlPreferences!!.vibrationOn()
        newButtonsStyle.isChecked = nhlPreferences!!.isNewButtonStyleActive

        seekBar.min = 5
        seekBar.max = 9

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                buttonsCount = progress
                seekBarValue.text = progress.toString()
                Log.d("SeekBar", "TextView text set to: ${seekBarValue.text}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBar.progress = nhlPreferences!!.customButtonsCount
        seekBarValue.text = nhlPreferences!!.customButtonsCount.toString() // just leave it here...

        overlayCheckbox.isChecked = nhlPreferences!!.isButtonOverlayActive

        val currentLanguageCode = nhlPreferences!!.languageLocale()
        if (currentLanguageCode == "pl") {
            powerSpinnerView.selectItemByIndex(1)
        } else if (currentLanguageCode == "en") {
            powerSpinnerView.selectItemByIndex(0)
        }
        when (nhlPreferences!!.sortingMode()) {
            null -> {
                powerSpinnerView2.selectItemByIndex(0)
            }

            "USAGE DESC" -> {
                powerSpinnerView2.selectItemByIndex(1)
            }

            "CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC" -> {
                powerSpinnerView2.selectItemByIndex(2)
            }

            "CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC" -> {
                powerSpinnerView2.selectItemByIndex(3)
            }

            "CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC" -> {
                powerSpinnerView2.selectItemByIndex(4)
            }

            "CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC" -> {
                powerSpinnerView2.selectItemByIndex(5)
            }
        }
        newButtonsStyle.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            VibrationUtils.vibrate()
        }
        vibrationsCheckbox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            VibrationUtils.vibrate()
        }
        overlayCheckbox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            VibrationUtils.vibrate()
        }

        ColorChanger.setContainerBackground(spinnerBg1, true)
        ColorChanger.setContainerBackground(spinnerBg2, true)

        powerSpinnerView.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                if (newIndex == 0) {
                    saveNhlLanguageTemp("DESCRIPTION_EN", "en")
                } else if (newIndex == 1) {
                    saveNhlLanguageTemp("DESCRIPTION_PL", "pl")
                }
            })
        powerSpinnerView2.setOnSpinnerItemSelectedListener(
            OnSpinnerItemSelectedListener { _: Int, _: String?, newIndex: Int, _: String? ->
                when (newIndex) {
                    0 -> {
                        saveNhlSettings(null)
                    }

                    1 -> {
                        saveNhlSettingsTemp("USAGE DESC")
                    }

                    2 -> {
                        saveNhlSettingsTemp("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC")
                    }

                    3 -> {
                        saveNhlSettingsTemp("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC")
                    }

                    4 -> {
                        saveNhlSettingsTemp("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC")
                    }

                    5 -> {
                        saveNhlSettingsTemp("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC")
                    }
                }
            })
        val updateCheckerUtils = UpdateCheckerUtils()
        checkUpdate.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            checkUpdate.text = requireActivity().resources.getString(R.string.update_wait)
            // Create an instance of UpdateCheckerUtils
            val updateCheckListener = object : UpdateCheckerUtils.UpdateCheckListener {
                override fun onUpdateCheckCompleted(updateResult: UpdateCheckResult?) {
                    // Run on the UI thread to update the UI components
                    requireActivity().lifecycleScope.launch {
                        if (updateResult != null) {
                            Log.d("testmessageout", "msg: " + updateResult.message)
                        }
                        if (updateResult != null) {
                            checkUpdate.text = updateResult.message
                        }
                        if (updateResult != null) {
                            if (updateResult.isUpdateAvailable) {
                                showCustomToast(
                                    requireActivity(),
                                    requireActivity().resources.getString(R.string.update_avaiable)
                                )
                                updateButton.visibility = View.VISIBLE
                                val rainbowColors = IntArray(100)
                                for (i in 0..99) {
                                    val hue = i.toFloat() / 100 * 360 // Distribute hues evenly
                                    rainbowColors[i] =
                                        Color.HSVToColor(floatArrayOf(hue, 1.0f, 1.0f))
                                }
                                val colorAnimator = ValueAnimator.ofArgb(*rainbowColors)
                                colorAnimator.setDuration(5000)
                                colorAnimator.interpolator = LinearInterpolator()
                                colorAnimator.repeatCount = ValueAnimator.INFINITE
                                colorAnimator.repeatMode = ValueAnimator.RESTART
                                colorAnimator.addUpdateListener { animation: ValueAnimator ->
                                    val animatedColor = animation.animatedValue as Int
                                    updateButton.setBackgroundColor(animatedColor)
                                }
                                colorAnimator.start()
                            } else {
                                updateButton.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            updateCheckerUtils.checkUpdateAsync(updateCheckListener)
        }

        updateButton.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://github.com/cr4sh-me/NHLauncher/releases/latest"))
            startActivity(intent)
        }
        runSetup.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            mainUtils!!.runCmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit")
        }
        backupDb.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            val dbb = DBBackup()
            mainActivity?.lifecycleScope?.launch {
                dbb.createBackup(requireContext())
            }
        }
        restoreDb.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            val dbb = DBBackup()
            mainActivity?.lifecycleScope?.launch {
                dbb.restoreBackup(requireContext())
            }
        }
        saveButton.setOnClickListener {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            mainActivity?.lifecycleScope?.launch {
                applySettings()
            }
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applySettings() {
        lifecycleScope.launch(Dispatchers.Default) {
            if (mainActivity != null) {
                VibrationUtils.vibrate()
            }
            // Apply the settings to SharedPreferences
            saveVibrationsPref(vibrationsCheckbox.isChecked)
            saveNewButtonPref(newButtonsStyle.isChecked)
            saveOverlayPref(overlayCheckbox.isChecked)
            if (newSortingModeSetting != null) {
                saveNhlSettings(newSortingModeSetting)
                newSortingModeSetting = null.toString()
            }
            if (newLanguageNameSetting != null && newLanguageLocaleSetting != null) {
                saveNhlLanguage(newLanguageNameSetting!!, newLanguageLocaleSetting!!)
                newLanguageNameSetting = null.toString()
                newLanguageLocaleSetting = null.toString()
            }

            buttonsCount?.let { saveButtonsCount(it) }

            lifecycleScope.launch {
                mainActivity?.recreate()
                requireActivity().recreate()
                showCustomToast(requireActivity(), "Settings updated!")
            }
        }
    }

    private fun saveNhlSettings(sortingMode: String?) {

        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("sortingMode", sortingMode)
        editor.apply()

//        mainUtils.restartSpinner();
    }

    private fun saveNhlLanguage(languageName: String, languageLocale: String) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("language", languageName)
        editor.putString("languageLocale", languageLocale)
        editor.apply()

        nhlPreferences?.languageLocale()?.let { languageChanger.setLocale(mainActivity, it) }
    }

    private fun saveVibrationsPref(vibrations: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("vibrationsOn", vibrations)
        editor.apply()
    }

    private fun saveNewButtonPref(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isNewButtonStyleActive", active)
        editor.apply()
    }

    private fun saveOverlayPref(active: Boolean) {
        // Save the color values and frame drawable to SharedPreferences
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isButtonOverlayActive", active)
        editor.apply()
        //        mainActivity.recreate();
    }

    private fun saveNhlSettingsTemp(sortingMode: String) {
        newSortingModeSetting = sortingMode
    }

    private fun saveNhlLanguageTemp(languageName: String, languageLocale: String) {
        newLanguageNameSetting = languageName
        newLanguageLocaleSetting = languageLocale
    }

    private fun saveButtonsCount(count: Int) {
        val prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("customButtonsCount", count)
        editor.apply()
    }
}