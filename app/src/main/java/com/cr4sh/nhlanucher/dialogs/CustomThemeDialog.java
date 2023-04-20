package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.DialogUtils;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class CustomThemeDialog extends DialogFragment {
    private String selectedTheme;
    private String selectedFont;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_theme_dialog, container, false);

        final String[] THEME_OPTIONS = {requireActivity().getResources().getString(R.string.choose_theme), "NHLauncher", "NetHunter", "Spaceship", "Mokambe", "Lime Gray", "UwU", "Tokyo", "Heaven's Gate", "Bricked", "Andrax"};
        final String[] FONT_OPTIONS = {requireActivity().getResources().getString(R.string.choose_font), "Roboto", "Default bold", "FiraCode", "FiraCode bold", "Montserrat", "Montserrat bold"};

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Spinner themeSpinner = view.findViewById(R.id.theme_spinner);
        Spinner fontSpinner = view.findViewById(R.id.font_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        view.setBackgroundResource(frame);
        cancelButton.setTextColor(Color.parseColor(nameColor));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, THEME_OPTIONS);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, FONT_OPTIONS);

        themeSpinner.setAdapter(adapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long i) {
                selectedTheme = adapterView.getItemAtPosition(position).toString();
                switch (selectedTheme) {
                    case "NetHunter":
                        saveThemeSettings("#5A5A5C", "#FFFFFF", "#A7FFEB", "frame", "#5A5A5C", "nhlauncher_nh");
                        break;
                    case "UwU":
                        saveThemeSettings("#ff8b9d", "#FFFFFF", "#b24250", "frame2", "#b24250", "nhlauncher_uwu");
                        break;
                    case "Lime Gray":
                        saveThemeSettings("#5A5A5C", "#7FFFD4", "#AAFF00", "frame3", "#5A5A5C", "nhlauncher_lime");
                        break;
                    case "Mokambe":
                        saveThemeSettings("#006400", "#FFD700", "#FFD700", "frame4", "#FFD700", "nhlauncher_mokambe");
                        break;
                    case "Spaceship":
                        saveThemeSettings("#000000", "#00dfff", "#00dfff", "frame5", "#00dfff", "nhlauncher_spaceship2");
                        break;
                    case "NHLauncher":
                        saveThemeSettings("#4A4A4C", "#FFFFFF", "#e94b3c", "frame6", "#4A4A4C", "nhlauncher");
                        break;
                    case "Tokyo":
                        saveThemeSettings("#000000", "#FF10F0", "#8969f7", "frame7", "#FF10F0", "nhlauncher_tokyo2");
                        break;
                    case "Heaven's Gate":
                        saveThemeSettings("#d1c7b7", "#f7f7ff", "#6c5b7b", "frame8", "#d1c7b7", "nhlauncher_heavensgate");
                        break;
                    case "Bricked":
                        saveThemeSettings("#333333", "#FFFFFF", "#ff9100", "frame9", "#1e1e1e", "nhlauncher_testorange2");
                        break;
                    case "Andrax":
                        saveThemeSettings("#000000", "#23FA14", "#23FA14", "frame10", "#23FA14", "nhlauncher_cybergreen");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        fontSpinner.setAdapter(adapter2);
        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedFont = adapterView.getItemAtPosition(position).toString();
                switch (selectedFont) {
                    case "Roboto":
                        saveFontSettings("roboto");
                        break;
                    case "FiraCode":
                        saveFontSettings("firacode");
                        break;
                    case "Default bold":
                        saveFontSettings("roboto_bold");
                        break;
                    case "FiraCode bold":
                        saveFontSettings("firacode_bold");
                        break;
                    case "Montserrat":
                        saveFontSettings("montserrat");
                        break;
                    case "Montserrat bold":
                        saveFontSettings("montserrat_bold");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancelButton.setOnClickListener(view1 -> dismiss());
        return view;
    }

    private void saveThemeSettings(String buttonColor, String nameColor, String descriptionColor, String frameColor, String strokeColor, String logoIcon) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("buttonColor", buttonColor);
        editor.putString("nameColor", nameColor);
        editor.putString("descriptionColor", descriptionColor);
        editor.putInt("buttonRadius", 10);
        editor.putString("frameColor", frameColor);
        editor.putString("strokeColor", strokeColor);
        editor.putString("logoIcon", logoIcon);
        editor.apply();

        // Restart dialog and apply changes!
        MainUtils.readColors();
        MainUtils.restartSpinner();
        MainUtils.refreshFrame();

        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());
        Objects.requireNonNull(getDialog()).cancel();
        dialogUtils.openCustomThemesDialog();
    }

    private void saveFontSettings(String fontName) {
        // Save the font values
        SharedPreferences prefs = requireActivity().getSharedPreferences("customFonts", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fontName", fontName);
        editor.apply();

        MainUtils.readColors();
        MainUtils.restartSpinner();
    }
}
