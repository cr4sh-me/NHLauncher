package com.cr4sh.nhlauncher.SettingsPager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.DBBackup;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.UpdateChecker;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class SettingsFragment1 extends Fragment {
    MyPreferences myPreferences;
    MainUtils mainUtils;
    MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private Button updateButton;

    public SettingsFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout1, container, false);

        myPreferences = new MyPreferences(requireActivity());
        mainUtils = new MainUtils(mainActivity);


        CheckBox vibrationsCheckbox = view.findViewById(R.id.vibrations_checkbox);
        CheckBox newButtonsStyle = view.findViewById(R.id.newbuttons_checkbox);
        TextView title = view.findViewById(R.id.bt_info2);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Button runSetup = view.findViewById(R.id.run_setup);
        Button backupDb = view.findViewById(R.id.db_backup);
        Button restoreDb = view.findViewById(R.id.db_restore);
        updateButton = view.findViewById(R.id.update_button);
        TextView checkUpdate = view.findViewById(R.id.checkUpdate);

        PowerSpinnerView powerSpinnerView = view.findViewById(R.id.language_spinner);
        PowerSpinnerView powerSpinnerView2 = view.findViewById(R.id.sorting_spinner);

        LinearLayout spinnerBg1 = view.findViewById(R.id.spinnerBg1);
        LinearLayout spinnerBg2 = view.findViewById(R.id.spinnerBg2);

        checkUpdate.setTextColor(Color.parseColor(myPreferences.color80()));

        powerSpinnerView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        powerSpinnerView.setTextColor(Color.parseColor(myPreferences.color80()));
        powerSpinnerView.setHintTextColor(Color.parseColor(myPreferences.color50()));
        powerSpinnerView.setDividerColor(Color.parseColor(myPreferences.color80()));

        powerSpinnerView2.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        powerSpinnerView2.setTextColor(Color.parseColor(myPreferences.color80()));
        powerSpinnerView2.setHintTextColor(Color.parseColor(myPreferences.color50()));
        powerSpinnerView2.setDividerColor(Color.parseColor(myPreferences.color80()));

        vibrationsCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        newButtonsStyle.setTextColor(Color.parseColor(myPreferences.color80()));
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(vibrationsCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(newButtonsStyle, new ColorStateList(states, colors));

        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        setButtonColors(runSetup);
        setButtonColors(backupDb);
        setButtonColors(restoreDb);

        vibrationsCheckbox.setChecked(myPreferences.vibrationOn());
        newButtonsStyle.setChecked(myPreferences.isNewButtonStyleActive());

        newButtonsStyle.setOnCheckedChangeListener(((buttonView, isChecked) -> saveNewButtonPref(isChecked)));
        vibrationsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> saveVibrationsPref(isChecked));

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(8, Color.parseColor(myPreferences.color50())); // Stroke width and color
        gd.setCornerRadius(20);
        spinnerBg1.setBackground(gd);
        spinnerBg2.setBackground(gd);

        powerSpinnerView.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
          if(newIndex == 0){
              saveNhlLanguage("DESCRIPTION_EN", "en");
          } else if (newIndex == 1) {
              saveNhlLanguage("DESCRIPTION_PL", "pl");
          }
        });

        powerSpinnerView2.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            if(newIndex == 0){
                saveNhlSettings(null);
            } else if (newIndex == 1) {
                saveNhlSettings("USAGE DESC");
            } else if (newIndex == 2) {
                saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC");
            } else if (newIndex == 3) {
                saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC");
            } else if (newIndex == 4) {
                saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC");
            } else if (newIndex == 5) {
                saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC");
            }
        });

        // Create an instance of UpdateChecker
        UpdateChecker updateChecker = new UpdateChecker(mainActivity);

        updateChecker.checkUpdateAsync(updateResult -> {
            // Run on the UI thread to update the UI components
            requireActivity().runOnUiThread(() -> {
                checkUpdate.setText(updateResult.message());

                if (updateResult.isUpdateAvailable()) {
                    Toast.makeText(requireActivity(), requireActivity().getResources().getString(R.string.update_avaiable), Toast.LENGTH_SHORT).show();
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


        updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/cr4sh-me/NHLauncher/releases/latest"));
            startActivity(intent);
        });

        runSetup.setOnClickListener(v -> mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit"));

        backupDb.setOnClickListener(v -> {
            DBBackup dbb = new DBBackup();
            new Thread(() -> {
                Looper.prepare();
                dbb.createBackup(getContext());
                Looper.loop();
            }).start();
        });

        restoreDb.setOnClickListener(view1 -> {
            DBBackup dbb = new DBBackup();
            new Thread(() -> {
                Looper.prepare();
                dbb.restoreBackup(getContext());
                Looper.loop();
            }).start();
        });

        return view;
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        button.setTextColor(Color.parseColor(myPreferences.color80()));
    }



    private void saveNhlSettings(String sortingMode) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sortingMode", sortingMode);
        editor.apply();

        mainUtils.restartSpinner();
    }

    private void saveNhlLanguage(String languageName, String languageLocale) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageName);
        editor.putString("languageLocale", languageLocale);
        editor.apply();

        mainUtils.changeLanguage(languageLocale);
        requireActivity().recreate();
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
        mainActivity.recreate();
    }

}