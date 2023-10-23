package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.widget.CompoundButtonCompat;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.Objects;


public class CustomThemeDialog extends AppCompatDialogFragment {

//    MainActivity myActivity;
    String hexColorString;

//    public CustomThemeDialog(MainActivity activity) {
//        this.myActivity = activity;
//    }
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_theme_dialog, container, false);

//        MainUtils mainUtils = new MainUtils(myActivity);
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        TextView title = view.findViewById(R.id.dialog_title);
        TextView text2  = view.findViewById(R.id.text_second);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
        BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.brightnessSlideBar);
        AlphaTileView alphaTileView1 = view.findViewById(R.id.alphaTileView1);
        AlphaTileView alphaTileView2 = view.findViewById(R.id.alphaTileView2);
        AlphaTileView alphaTileView3 = view.findViewById(R.id.alphaTileView3);
        AlphaTileView alphaTileView4 = view.findViewById(R.id.alphaTileView4);
        EditText hexColorValue = view.findViewById(R.id.customHexColor);
        CheckBox dynamicThemes = view.findViewById(R.id.dynamic_themes_checkbox);
        Button applyColors = view.findViewById(R.id.apply_custom_colors);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Checkbox set

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            dynamicThemes.setChecked(myPreferences.dynamicThemeBool());
        } else {
            dynamicThemes.setEnabled(false);
        }




        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));
        text2.setTextColor(Color.parseColor(myPreferences.color80()));
        dynamicThemes.setTextColor(Color.parseColor(myPreferences.color80()));

        int [][] states = {{android.R.attr.state_checked}, {}};
        int [] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(dynamicThemes, new ColorStateList(states, colors));

        hexColorValue.setTextColor(Color.parseColor(myPreferences.color80()));

        applyColors.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        applyColors.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));


        hexColorValue.setEnabled(false);

//        brightnessSlideBar.
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        BubbleFlag at = new BubbleFlag(requireActivity());
        at.setFlagMode(FlagMode.ALWAYS);
        colorPickerView.setFlagView(at);


//        colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.kali_hashid));

        colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));

        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            ColorEnvelope colorEnvelope = new ColorEnvelope(color);
            hexColorValue.setText("#" + colorEnvelope.getHexCode());
            hexColorString = "#" + colorEnvelope.getHexCode();
            alphaTileView1.setPaintColor(color);
            alphaTileView2.setPaintColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
            alphaTileView3.setPaintColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.6f)));
            alphaTileView4.setPaintColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));
        });
        
        applyColors.setOnClickListener(v -> {
            String color100 = hexColorString;
            String color80 = adjustColorBrightness(hexColorString, 0.8f);
            String color50 = adjustColorBrightness(hexColorString, 0.5f);
            String color20 = adjustColorBrightness(hexColorString, 0.2f);
//            String color20 = String.valueOf(adjustColorBrightness(hexColorString, 0.1f));
//
//            Toast.makeText(requireActivity(), "Saving: " + color20, Toast.LENGTH_SHORT).show();
            saveColor80(color100, color80, color50, color20, dynamicThemes.isChecked());
            requireActivity().recreate();


//            myPreferences
//// Convert hex string to color integer
//            int customColor = Color.parseColor(hexColorString);
//
//            TypedValue typedValue = new TypedValue();
//
//// Create a custom theme
//            Resources.Theme customTheme = getResources().newTheme();
//            customTheme.applyStyle(R.style.Theme_NHL, true);
//
//// Change the color dynamically
//            typedValue.data = customColor;
//
//// Apply the custom color to the theme
//            customTheme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);



        });
        
        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }

        public static String adjustColorBrightness(String hexColor, float factor) {
        // https://github.com/edelstone/tints-and-shades

//            SHADES
//            #663399 is converted to the RGB equivalent of 102, 51, 153
//            R: 102 x .9 = 91.8, rounded to 92
//            G: 51 x .9 = 45.9, rounded to 46
//            B: 153 x .9 = 137.7, rounded to 138
//            RGB 92, 46, 138 is converted to the hex equivalent of #5c2e8a


            int color = Color.parseColor(hexColor);
//            int color = Color.parseColor("#663399");
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            Log.d("XDD", "R:" + r + "G:" + g + "B:" + b);

//            Log.d("XDD", String.valueOf(factor));
            double r_math = r * factor;
            double g_math = g * factor;
            double b_math = b * factor;
            Log.d("XDD", String.format("#%02X%02X%02X", Math.round(r_math), Math.round(g_math), Math.round(b_math)));
            return String.format("#%02X%02X%02X", Math.round(r_math), Math.round(g_math), Math.round(b_math));
        }



    private void saveColor80(String color100, String color80, String color50, String color20, boolean dynamicThemeBool) {
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("customColors", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("color100", color100);
        editor.putString("color80", color80);
        editor.putString("color50", color50);
        editor.putString("color20", color20);
        editor.putBoolean("dynamicThemeBool", dynamicThemeBool);
        editor.apply();
    }
}
