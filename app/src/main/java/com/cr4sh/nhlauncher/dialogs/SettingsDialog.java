package com.cr4sh.nhlauncher.dialogs;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlauncher.CustomSpinnerAdapter;
import com.cr4sh.nhlauncher.DBBackup;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.UpdateChecker;
import com.cr4sh.nhlauncher.utils.MainUtils;

import java.util.Arrays;
import java.util.List;

public class SettingsDialog extends DialogFragment {
    private String selectedSorting;
    private String selectedLanguage;
    private MainUtils mainUtils;
    private Button updateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog, container, false);

        mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        CheckBox vibrationsCheckbox = view.findViewById(R.id.vibrations_checkbox);
        CheckBox newButtonsStyle = view.findViewById(R.id.newbuttons_checkbox);
        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Spinner sortingSpinner = view.findViewById(R.id.sorting_spinner);
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button runSetup = view.findViewById(R.id.run_setup);
        Button backupDb = view.findViewById(R.id.db_backup);
        Button restoreDb = view.findViewById(R.id.db_restore);
        updateButton = view.findViewById(R.id.update_button);
        TextView checkUpdate = view.findViewById(R.id.checkUpdate);

        checkUpdate.setTextColor(Color.parseColor(myPreferences.color80()));

        GradientDrawable drawableToolbar = new GradientDrawable();
        drawableToolbar.setCornerRadius(100);
        drawableToolbar.setStroke(8, Color.parseColor(myPreferences.color50()));
        languageSpinner.setBackground(drawableToolbar);
        sortingSpinner.setBackground(drawableToolbar);

        List<String> valuesList = Arrays.asList(
                requireActivity().getResources().getString(R.string.sorting),
                requireActivity().getResources().getString(R.string.default_sorting),
                requireActivity().getResources().getString(R.string.by_usage),
                "A-Z",
                "Z-A",
                "0-9 A-Z",
                "9-0 Z-A"
        );

        List<String> valuesList2 = Arrays.asList(
                requireActivity().getResources().getString(R.string.choose_lanuage),
                "English",
                "Polish"
        );

        List<Integer> imageList = List.of();

        CustomSpinnerAdapter customSpinnerAdapter1 = new CustomSpinnerAdapter(requireActivity(), valuesList, imageList, myPreferences.color20(), myPreferences.color80());
        CustomSpinnerAdapter customSpinnerAdapter2 = new CustomSpinnerAdapter(requireActivity(), valuesList2, imageList, myPreferences.color20(), myPreferences.color80());

        // Apply custom themes
        vibrationsCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        newButtonsStyle.setTextColor(Color.parseColor(myPreferences.color80()));
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(vibrationsCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(newButtonsStyle, new ColorStateList(states, colors));

        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        runSetup.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        runSetup.setTextColor(Color.parseColor(myPreferences.color80()));
        backupDb.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        backupDb.setTextColor(Color.parseColor(myPreferences.color80()));
        restoreDb.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        restoreDb.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        vibrationsCheckbox.setChecked(myPreferences.vibrationOn());
        newButtonsStyle.setChecked(myPreferences.isNewButtonStyleActive());

        newButtonsStyle.setOnCheckedChangeListener(((buttonView, isChecked) -> saveNewButtonPref(isChecked)));
        vibrationsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> saveVibrationsPref(isChecked));

        // Create an instance of UpdateChecker
        UpdateChecker updateChecker = new UpdateChecker((MainActivity) requireActivity());

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

        sortingSpinner.setAdapter(customSpinnerAdapter1);
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long i) {
                selectedSorting = adapterView.getItemAtPosition(position).toString();
                switch (selectedSorting) {
                    case "Default", "Domyślne" -> saveNhlSettings(null);
                    case "By usage", "Po użyciu" -> saveNhlSettings("USAGE DESC");
                    case "A-Z" ->
                            saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC");
                    case "Z-A" ->
                            saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC");
                    case "0-9 A-Z" ->
                            saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC");
                    case "9-0 Z-A" ->
                            saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        languageSpinner.setAdapter(customSpinnerAdapter2);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
                switch (selectedLanguage) {
                    case "English" -> {
                        languageSpinner.setSelection(0);
                        saveNhlLanguage("DESCRIPTION_EN", "en");
                    }
                    case "Polish" -> {
                        languageSpinner.setSelection(0);
                        saveNhlLanguage("DESCRIPTION_PL", "pl");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        runSetup.setOnClickListener(v -> mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit"));

        cancelButton.setOnClickListener(view12 -> dismiss());

        // DB BACKUP
        backupDb.setOnClickListener(v -> {
            DBBackup dbb = new DBBackup((MainActivity) requireActivity());
            new Thread(() -> {
                Looper.prepare();
                dbb.createBackup(getContext());
                Looper.loop();
            }).start();
        });


        restoreDb.setOnClickListener(view1 -> {
            DBBackup dbb = new DBBackup((MainActivity) requireActivity());
            new Thread(() -> {
                Looper.prepare();
                dbb.restoreBackup(getContext());
                Looper.loop();
            }).start();
        });

        return view;
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
        requireActivity().recreate();
    }
}

