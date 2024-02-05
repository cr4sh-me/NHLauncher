package com.cr4sh.nhlauncher.settingsPager

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.MainActivity
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.database.DBBackup
import com.cr4sh.nhlauncher.utils.MainUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils.showCustomToast
import com.cr4sh.nhlauncher.utils.UpdateCheckerUtils
import com.cr4sh.nhlauncher.utils.UpdateCheckerUtils.UpdateCheckResult
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.launch

class SettingsFragment1 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    var mainUtils: MainUtils? = null
    val mainActivity: MainActivity = NHLManager.instance.mainActivity
    private lateinit var updateButton: Button
    private var newSortingModeSetting: String? = null
    private var newLanguageNameSetting: String? = null
    private var newLanguageLocaleSetting: String? = null
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
        mainUtils = MainUtils(mainActivity)
        vibrationsCheckbox = view.findViewById(R.id.vibrations_checkbox)
        newButtonsStyle = view.findViewById(R.id.newbuttons_checkbox)
        overlayCheckbox = view.findViewById(R.id.overlay_checkbox)
        val title = view.findViewById<TextView>(R.id.bt_info2)
        val bkg = view.findViewById<LinearLayout>(R.id.custom_theme_dialog_background)
        val runSetup = view.findViewById<Button>(R.id.run_setup)
        val backupDb = view.findViewById<Button>(R.id.db_backup)
        val restoreDb = view.findViewById<Button>(R.id.db_restore)
        val saveButton = view.findViewById<Button>(R.id.save_button)
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
        powerSpinnerView.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView.dividerColor = Color.parseColor(nhlPreferences!!.color80())
        powerSpinnerView2.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        powerSpinnerView2.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        powerSpinnerView2.setHintTextColor(Color.parseColor(nhlPreferences!!.color50()))
        powerSpinnerView2.dividerColor = Color.parseColor(nhlPreferences!!.color80())
        vibrationsCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        newButtonsStyle.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        overlayCheckbox.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        vibrationsCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        newButtonsStyle.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        overlayCheckbox.buttonTintList = ColorStateList.valueOf(
            Color.parseColor(
                nhlPreferences!!.color80()
            )
        )
        bkg.setBackgroundColor(Color.parseColor(nhlPreferences!!.color20()))
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        setButtonColors(runSetup)
        setButtonColors(backupDb)
        setButtonColors(restoreDb)
        setButtonColors(saveButton)
        vibrationsCheckbox.isChecked = nhlPreferences!!.vibrationOn()
        newButtonsStyle.isChecked = nhlPreferences!!.isNewButtonStyleActive
        overlayCheckbox.isChecked = nhlPreferences!!.isButtonOverlayActive
        val resources = mainActivity.resources
        val configuration = resources?.configuration
        val localeList = configuration?.locales
        val currentLocale = localeList?.get(0)
        val currentLanguageCode = currentLocale?.language
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
            vibrate(
                requireActivity(),
                10
            )
        }
        vibrationsCheckbox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            vibrate(
                requireActivity(),
                10
            )
        }
        overlayCheckbox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            vibrate(
                requireActivity(),
                10
            )
        }
        val gd = GradientDrawable()
        gd.setStroke(8, Color.parseColor(nhlPreferences!!.color50())) // Stroke width and color
        gd.cornerRadius = 20f
        spinnerBg1.background = gd
        spinnerBg2.background = gd
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
            vibrate(mainActivity, 10)
            checkUpdate.text = requireActivity().resources.getString(R.string.update_wait)
            // Create an instance of UpdateCheckerUtils
            val updateCheckListener = object : UpdateCheckerUtils.UpdateCheckListener {
                override fun onUpdateCheckCompleted(updateResult: UpdateCheckResult?) {
                    // Run on the UI thread to update the UI components
                    requireActivity().runOnUiThread {
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
            vibrate(mainActivity, 10)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://github.com/cr4sh-me/NHLauncher/releases/latest"))
            startActivity(intent)
        }
        runSetup.setOnClickListener {
            vibrate(mainActivity, 10)
            mainUtils!!.runCmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit")
        }
        backupDb.setOnClickListener {
            vibrate(mainActivity, 10)
            val dbb = DBBackup()
            mainActivity.lifecycleScope.launch {
                dbb.createBackup(requireContext())
            }
        }
        restoreDb.setOnClickListener {
            vibrate(mainActivity, 10)
            val dbb = DBBackup()
            mainActivity.lifecycleScope.launch {
                dbb.restoreBackup(requireContext())             }
        }
        saveButton.setOnClickListener {
            vibrate(mainActivity, 10)
            applySettings()
        }
        powerSpinnerView.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView.selectItemByIndex(powerSpinnerView.selectedIndex)
            }
        powerSpinnerView2.spinnerOutsideTouchListener =
            OnSpinnerOutsideTouchListener { _: View?, _: MotionEvent? ->
                powerSpinnerView2.selectItemByIndex(powerSpinnerView2.selectedIndex)
            }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applySettings() {
        vibrate(mainActivity, 10)

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
        mainActivity.recreate()
        requireActivity().recreate()
    }

    private fun setButtonColors(button: Button) {
        button.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        button.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
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
        mainUtils!!.changeLanguage(languageLocale)
        //        requireActivity().recreate();
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
        //        mainActivity.recreate();
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
}