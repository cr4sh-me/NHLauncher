package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.DBBackup;
import com.cr4sh.nhlanucher.MainActivity;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

public class SettingsDialog extends DialogFragment {
    private String selectedSorting;
    private String selectedLanguage;

    private MainUtils mainUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog, container, false);

        mainUtils = new MainUtils((MainActivity) requireActivity());

        final String[] SORTING_OPTIONS = {requireActivity().getResources().getString(R.string.sorting), requireActivity().getResources().getString(R.string.default_sorting), requireActivity().getResources().getString(R.string.by_usage), "A-Z", "Z-A", "0-9 A-Z", "9-0 Z-A"};
        final String[] LANGUAGE_OPTIONS = {requireActivity().getResources().getString(R.string.choose_lanuage), "English", "Polish"};

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Spinner sortingSpinner = view.findViewById(R.id.sorting_spinner);
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button runSetup = view.findViewById(R.id.run_setup);
        Button backupDb = view.findViewById(R.id.db_backup);
        Button restoreDb = view.findViewById(R.id.db_restore);

        view.setBackgroundResource(frame);
        runSetup.setTextColor(Color.parseColor(nameColor));
        backupDb.setTextColor(Color.parseColor(nameColor));
        restoreDb.setTextColor(Color.parseColor(nameColor));
        cancelButton.setTextColor(Color.parseColor(nameColor));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, SORTING_OPTIONS);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, LANGUAGE_OPTIONS);

        sortingSpinner.setAdapter(adapter);
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long i) {
                selectedSorting = adapterView.getItemAtPosition(position).toString();
                switch (selectedSorting) {
                    case "Default":
                    case "Domyślne":
                        saveNhlSettings(null);
                        break;
                    case "By usage":
                    case "Po użyciu":
                        saveNhlSettings("USAGE DESC");
                        break;
                    case "A-Z":
                        saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME ASC");
                        break;
                    case "Z-A":
                        saveNhlSettings("CASE WHEN NAME GLOB '[A-Za-z]*' THEN 0 ELSE 1 END, NAME DESC");
                        break;
                    case "0-9 A-Z":
                        saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME ASC");
                        break;
                    case "9-0 Z-A":
                        saveNhlSettings("CASE WHEN NAME GLOB '[0-9]*' THEN 0 ELSE 1 END, NAME COLLATE NOCASE DESC");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        languageSpinner.setAdapter(adapter2);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
                switch (selectedLanguage) {
                    case "English":
                        languageSpinner.setSelection(0);
                        saveNhlLanguage("DESCRIPTION_EN", "en");
                        break;
                    case "Polish":
                        languageSpinner.setSelection(0);
                        saveNhlLanguage("DESCRIPTION_PL", "pl");
                        break;
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

//        mainUtils.readSettings();
        mainUtils.restartSpinner();
    }

    private void saveNhlLanguage(String languageName, String languageLocale) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageName);
        editor.putString("languageLocale", languageLocale);
        editor.apply();

//        mainUtils.readSettings();
        mainUtils.changeLanguage(languageLocale);
        mainUtils.restartSpinner();
        requireActivity().recreate();

    }

}

