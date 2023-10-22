package com.cr4sh.nhlauncher.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlauncher.DBBackup;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MainUtils;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

public class SettingsDialog extends DialogFragment {
    private String selectedSorting;
    private String selectedLanguage;
    private MainUtils mainUtils;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog, container, false);

        mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        final String[] SORTING_OPTIONS = {requireActivity().getResources().getString(R.string.sorting), requireActivity().getResources().getString(R.string.default_sorting), requireActivity().getResources().getString(R.string.by_usage), "A-Z", "Z-A", "0-9 A-Z", "9-0 Z-A"};
        final String[] LANGUAGE_OPTIONS = {requireActivity().getResources().getString(R.string.choose_lanuage), "English", "Polish"};


        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Spinner sortingSpinner = view.findViewById(R.id.sorting_spinner);
        Spinner languageSpinner = view.findViewById(R.id.language_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button runSetup = view.findViewById(R.id.run_setup);
        Button backupDb = view.findViewById(R.id.db_backup);
        Button restoreDb = view.findViewById(R.id.db_restore);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, SORTING_OPTIONS);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, LANGUAGE_OPTIONS);

        // Apply custom themes
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
        mainUtils.restartSpinner();
        requireActivity().recreate();

    }

}

