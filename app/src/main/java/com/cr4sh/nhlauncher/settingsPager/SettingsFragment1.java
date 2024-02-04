package com.cr4sh.nhlauncher.settingsPager;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.database.DBBackup;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.UpdateCheckerUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class SettingsFragment1 extends Fragment {
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();
    NHLPreferences NHLPreferences;
    MainUtils mainUtils;
    MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private Button updateButton;
    private String newSortingModeSetting;
    private String newLanguageNameSetting;
    private String newLanguageLocaleSetting;
    private CheckBox vibrationsCheckbox;
    private CheckBox newButtonsStyle;
    private CheckBox overlayCheckbox;

    public SettingsFragment1() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout1, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());
        mainUtils = new MainUtils(mainActivity);

        vibrationsCheckbox = view.findViewById(R.id.vibrations_checkbox);
        newButtonsStyle = view.findViewById(R.id.newbuttons_checkbox);
        overlayCheckbox = view.findViewById(R.id.overlay_checkbox);
        TextView title = view.findViewById(R.id.bt_info2);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Button runSetup = view.findViewById(R.id.run_setup);
        Button backupDb = view.findViewById(R.id.db_backup);
        Button restoreDb = view.findViewById(R.id.db_restore);
        Button saveButton = view.findViewById(R.id.save_button);
        updateButton = view.findViewById(R.id.update_button);
        TextView checkUpdate = view.findViewById(R.id.checkUpdate);


        TextView spinnerText1 = view.findViewById(R.id.language_spinner_label);
        TextView spinnerText2 = view.findViewById(R.id.sorting_spinner_label);

        spinnerText1.setTextColor(Color.parseColor(NHLPreferences.color80()));
        spinnerText2.setTextColor(Color.parseColor(NHLPreferences.color80()));

        PowerSpinnerView powerSpinnerView = view.findViewById(R.id.language_spinner);
        PowerSpinnerView powerSpinnerView2 = view.findViewById(R.id.sorting_spinner);

        LinearLayout spinnerBg1 = view.findViewById(R.id.spinnerBg1);
        LinearLayout spinnerBg2 = view.findViewById(R.id.spinnerBg2);

        checkUpdate.setTextColor(Color.parseColor(NHLPreferences.color80()));

        powerSpinnerView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        powerSpinnerView.setTextColor(Color.parseColor(NHLPreferences.color80()));
        powerSpinnerView.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        powerSpinnerView.setDividerColor(Color.parseColor(NHLPreferences.color80()));

        powerSpinnerView2.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        powerSpinnerView2.setTextColor(Color.parseColor(NHLPreferences.color80()));
        powerSpinnerView2.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        powerSpinnerView2.setDividerColor(Color.parseColor(NHLPreferences.color80()));

        vibrationsCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));
        newButtonsStyle.setTextColor(Color.parseColor(NHLPreferences.color80()));
        overlayCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));

        vibrationsCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));
        newButtonsStyle.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));
        overlayCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));


        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        setButtonColors(runSetup);
        setButtonColors(backupDb);
        setButtonColors(restoreDb);
        setButtonColors(saveButton);

        vibrationsCheckbox.setChecked(NHLPreferences.vibrationOn());
        newButtonsStyle.setChecked(NHLPreferences.isNewButtonStyleActive());
        overlayCheckbox.setChecked(NHLPreferences.isButtonOverlayActive());


        Resources resources = mainActivity.getResources();
        Configuration configuration = resources.getConfiguration();

        LocaleList localeList = configuration.getLocales();
        Locale currentLocale = localeList.get(0);
        String currentLanguageCode = currentLocale.getLanguage();

        if (currentLanguageCode.equals("pl")) {
            powerSpinnerView.selectItemByIndex(1);
        } else if (currentLanguageCode.equals("en")) {
            powerSpinnerView.selectItemByIndex(0);
        }

        String sortingMode = NHLPreferences.sortingMode();
        if (sortingMode == null) {
            powerSpinnerView2.selectItemByIndex(0);
        } else if (sortingMode.equals("USAGE DESC")) {
            powerSpinnerView2.selectItemByIndex(1);
        } else if (sortingMode.equals("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC")) {
            powerSpinnerView2.selectItemByIndex(2);
        } else if (sortingMode.equals("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC")) {
            powerSpinnerView2.selectItemByIndex(3);
        } else if (sortingMode.equals("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC")) {
            powerSpinnerView2.selectItemByIndex(4);
        } else if (sortingMode.equals("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC")) {
            powerSpinnerView2.selectItemByIndex(5);
        }

        newButtonsStyle.setOnCheckedChangeListener((buttonView, isChecked) -> VibrationUtils.vibrate(requireActivity(), 10));
        vibrationsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> VibrationUtils.vibrate(requireActivity(), 10));
        overlayCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> VibrationUtils.vibrate(requireActivity(), 10));


        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(8, Color.parseColor(NHLPreferences.color50())); // Stroke width and color
        gd.setCornerRadius(20);
        spinnerBg1.setBackground(gd);
        spinnerBg2.setBackground(gd);

        powerSpinnerView.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            if (newIndex == 0) {
                saveNhlLanguageTemp("DESCRIPTION_EN", "en");
            } else if (newIndex == 1) {
                saveNhlLanguageTemp("DESCRIPTION_PL", "pl");
            }
        });

        powerSpinnerView2.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            if (newIndex == 0) {
                saveNhlSettings(null);
            } else if (newIndex == 1) {
                saveNhlSettingsTemp("USAGE DESC");
            } else if (newIndex == 2) {
                saveNhlSettingsTemp("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC");
            } else if (newIndex == 3) {
                saveNhlSettingsTemp("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC");
            } else if (newIndex == 4) {
                saveNhlSettingsTemp("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC");
            } else if (newIndex == 5) {
                saveNhlSettingsTemp("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC");
            }
        });

        UpdateCheckerUtils updateCheckerUtils = new UpdateCheckerUtils();

        checkUpdate.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            checkUpdate.setText(requireActivity().getResources().getString(R.string.update_wait));
            // Create an instance of UpdateCheckerUtils

            updateCheckerUtils.checkUpdateAsync(updateResult -> {
                // Run on the UI thread to update the UI components
                requireActivity().runOnUiThread(() -> {
                    Log.d("testmessageout", "msg: " + updateResult.message());
                    checkUpdate.setText(updateResult.message());

                    if (updateResult.isUpdateAvailable()) {
                        ToastUtils.showCustomToast(requireActivity(), requireActivity().getResources().getString(R.string.update_avaiable));
                        updateButton.setVisibility(View.VISIBLE);

                        int[] rainbowColors = new int[100];

                        for (int i = 0; i < 100; i++) {
                            float hue = (float) i / 100 * 360; // Distribute hues evenly
                            rainbowColors[i] = Color.HSVToColor(new float[]{hue, 1.0f, 1.0f});
                        }

                        ValueAnimator colorAnimator = ValueAnimator.ofArgb(rainbowColors);
                        colorAnimator.setDuration(5000);
                        colorAnimator.setInterpolator(new LinearInterpolator());
                        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        colorAnimator.setRepeatMode(ValueAnimator.RESTART);

                        colorAnimator.addUpdateListener(animation -> {
                            int animatedColor = (int) animation.getAnimatedValue();
                            updateButton.setBackgroundColor(animatedColor);
                        });

                        colorAnimator.start();
                    } else {
                        updateButton.setVisibility(View.GONE);
                    }
                });
            });

        });


        updateButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/cr4sh-me/NHLauncher/releases/latest"));
            startActivity(intent);
        });

        runSetup.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit");
        });

        backupDb.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            DBBackup dbb = new DBBackup();
            executor.execute(() -> dbb.createBackup(requireContext()));
        });

        restoreDb.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            DBBackup dbb = new DBBackup();
            executor.execute(() -> dbb.restoreBackup(requireContext()));
        });

        saveButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            applySettings();
        });

        powerSpinnerView.setSpinnerOutsideTouchListener((view1, motionEvent) -> powerSpinnerView.selectItemByIndex(powerSpinnerView.getSelectedIndex()));
        powerSpinnerView2.setSpinnerOutsideTouchListener((view12, motionEvent) -> powerSpinnerView2.selectItemByIndex(powerSpinnerView2.getSelectedIndex()));

        return view;
    }

    private void applySettings() {
        VibrationUtils.vibrate(mainActivity, 10);

        // Apply the settings to SharedPreferences
        saveVibrationsPref(vibrationsCheckbox.isChecked());
        saveNewButtonPref(newButtonsStyle.isChecked());
        saveOverlayPref(overlayCheckbox.isChecked());


        if (newSortingModeSetting != null) {
            saveNhlSettings(newSortingModeSetting);
            newSortingModeSetting = null;
        }

        if (newLanguageNameSetting != null && newLanguageLocaleSetting != null) {
            saveNhlLanguage(newLanguageNameSetting, newLanguageLocaleSetting);
            newLanguageNameSetting = null;
            newLanguageLocaleSetting = null;
        }

        mainActivity.recreate();
        requireActivity().recreate();
    }


    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        button.setTextColor(Color.parseColor(NHLPreferences.color80()));
    }


    private void saveNhlSettings(String sortingMode) {

        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sortingMode", sortingMode);
        editor.apply();

//        mainUtils.restartSpinner();
    }

    private void saveNhlLanguage(String languageName, String languageLocale) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageName);
        editor.putString("languageLocale", languageLocale);
        editor.apply();

        mainUtils.changeLanguage(languageLocale);
//        requireActivity().recreate();
    }

    private void saveVibrationsPref(boolean vibrations) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("vibrationsOn", vibrations);
        editor.apply();
    }

    private void saveNewButtonPref(boolean active) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isNewButtonStyleActive", active);
        editor.apply();
//        mainActivity.recreate();
    }

    private void saveOverlayPref(boolean active) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isButtonOverlayActive", active);
        editor.apply();
//        mainActivity.recreate();
    }

    private void saveNhlSettingsTemp(String sortingMode) {
        newSortingModeSetting = sortingMode;
    }

    private void saveNhlLanguageTemp(String languageName, String languageLocale) {
        newLanguageNameSetting = languageName;
        newLanguageLocaleSetting = languageLocale;
    }
}