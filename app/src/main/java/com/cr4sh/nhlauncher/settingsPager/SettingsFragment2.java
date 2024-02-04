package com.cr4sh.nhlauncher.settingsPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;
import com.flask.colorpicker.ColorPickerView;

public class SettingsFragment2 extends Fragment {
    NHLPreferences NHLPreferences;
    String hexColorString;
    MainActivity mainActivity = NHLManager.getInstance().getMainActivity();


    public SettingsFragment2() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout2, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());

        LinearLayout manualBox = view.findViewById(R.id.hiddenLayout);
        LinearLayout advancedMode = view.findViewById(R.id.advancedLayout);
//        TextView title = view.findViewById(R.id.dialog_title);
        TextView text2 = view.findViewById(R.id.text_second);
//        ScrollView bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
//        ImageView alphaTileView1 = view.findViewById(R.id.alphaTileView1);

        RelativeLayout alphaLayout = view.findViewById(R.id.alphaLayout);
        ImageView alphaTileView1 = view.findViewById(R.id.alphaTileView1);
        ImageView alphaTileView2 = view.findViewById(R.id.alphaTileView2);
        ImageView alphaTileView3 = view.findViewById(R.id.alphaTileView3);

        RelativeLayout alphaLayoutAdv = view.findViewById(R.id.alphaLayoutAdv);
        ImageView alphaTileViewAdv1 = view.findViewById(R.id.alphaTileViewAdv1);
        ImageView alphaTileViewAdv2 = view.findViewById(R.id.alphaTileViewAdv2);
        ImageView alphaTileViewAdv3 = view.findViewById(R.id.alphaTileViewAdv3);

        Button hexColorValue1 = view.findViewById(R.id.customHexColor1);
        Button hexColorValue2 = view.findViewById(R.id.customHexColor2);
        Button hexColorValue3 = view.findViewById(R.id.customHexColor3);
        EditText hexColorValue = view.findViewById(R.id.customHexColor);
        CheckBox dynamicThemes = view.findViewById(R.id.dynamic_themes_checkbox);
        CheckBox advancedThemes = view.findViewById(R.id.advanced_themes_checkbox);
        Button applyColors = view.findViewById(R.id.apply_custom_colors);
//        Button cancelButton = view.findViewById(R.id.cancel_button);

        hexColorString = NHLPreferences.color100();
        hexColorValue.setText(hexColorString);


        // Checkbox set

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            dynamicThemes.setChecked(NHLPreferences.dynamicThemeBool());
        } else {
            dynamicThemes.setEnabled(false);
        }

        if (dynamicThemes.isChecked()) {
            manualBox.setVisibility(View.GONE);
        } else {
            manualBox.setVisibility(View.VISIBLE);
            colorPickerView.setInitialColor(Color.parseColor(NHLPreferences.color100()), true);
        }

        if (NHLPreferences.advancedThemeBool()) {
            advancedThemes.setChecked(true);
            advancedMode.setVisibility(View.VISIBLE);
            alphaLayout.setVisibility(View.GONE);
            hexColorValue.setVisibility(View.GONE);
            colorPickerView.setVisibility(View.GONE);
            hexColorValue1.setText(NHLPreferences.color80());
            hexColorValue2.setText(NHLPreferences.color50());
            hexColorValue3.setText(NHLPreferences.color20());

            alphaLayoutAdv.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorValue3.getText().toString(), 0.5f)));
            alphaTileViewAdv1.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
            alphaTileViewAdv2.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
            alphaTileViewAdv3.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));


        } else {
            advancedThemes.setChecked(false);
            advancedMode.setVisibility(View.GONE);
            alphaLayout.setVisibility(View.VISIBLE);
            hexColorValue.setVisibility(View.VISIBLE);
            colorPickerView.setVisibility(View.VISIBLE);
            colorPickerView.setInitialColor(Color.parseColor(NHLPreferences.color100()), true);

            alphaLayout.setBackgroundColor(Color.parseColor(adjustColorBrightness(NHLPreferences.color20(), 0.5f)));
            alphaTileView1.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
            alphaTileView2.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
            alphaTileView3.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        }

        // Apply custom themes
        text2.setTextColor(Color.parseColor(NHLPreferences.color80()));
        dynamicThemes.setTextColor(Color.parseColor(NHLPreferences.color80()));
        advancedThemes.setTextColor(Color.parseColor(NHLPreferences.color80()));

        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(NHLPreferences.color80()), Color.parseColor(NHLPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(dynamicThemes, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(advancedThemes, new ColorStateList(states, colors));

        hexColorValue.setTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));
        hexColorValue1.setTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue1.setHintTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue1.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));
        hexColorValue2.setTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue2.setHintTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue2.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));
        hexColorValue3.setTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue3.setHintTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue3.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));

        applyColors.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        applyColors.setTextColor(Color.parseColor(NHLPreferences.color80()));

        hexColorValue.setEnabled(false);

        dynamicThemes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                manualBox.setVisibility(View.GONE);
            } else {
                manualBox.setVisibility(View.VISIBLE);
//                colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
            }
        });

        advancedThemes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                advancedMode.setVisibility(View.VISIBLE);
                alphaLayout.setVisibility(View.GONE);
                hexColorValue.setVisibility(View.GONE);
                colorPickerView.setVisibility(View.GONE);
                hexColorValue1.setText(NHLPreferences.color80());
                hexColorValue2.setText(NHLPreferences.color50());
                hexColorValue3.setText(NHLPreferences.color20());

                alphaLayoutAdv.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorValue3.getText().toString(), 0.5f)));
                alphaTileViewAdv1.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
                alphaTileViewAdv2.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
                alphaTileViewAdv3.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));

            } else {
                advancedMode.setVisibility(View.GONE);
                alphaLayout.setVisibility(View.VISIBLE);
                hexColorValue.setVisibility(View.VISIBLE);
                colorPickerView.setVisibility(View.VISIBLE);
                colorPickerView.setInitialColor(Color.parseColor(NHLPreferences.color100()), true);

                alphaLayout.setBackgroundColor(Color.parseColor(adjustColorBrightness(NHLPreferences.color20(), 0.5f)));
                alphaTileView1.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
                alphaTileView2.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
                alphaTileView3.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
            }
        });

        hexColorValue1.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            openPickerDialog(hexColorValue1, alphaTileViewAdv1, NHLPreferences.color80());
        });
        hexColorValue2.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            openPickerDialog(hexColorValue2, alphaTileViewAdv2, NHLPreferences.color50());
        });
        hexColorValue3.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            openPickerDialog(hexColorValue3, alphaTileViewAdv3, NHLPreferences.color20());
        });

        colorPickerView.addOnColorChangedListener(selectedColor -> {
            hexColorValue.setText(("#" + Integer.toHexString(selectedColor)).toUpperCase());
            hexColorString = ("#" + Integer.toHexString(selectedColor)).toUpperCase();

//            alphaLayout.setBackgroundColor(Color.parseColor(adjustColorBrightness(NHLPreferences.color20(), 0.5f)));
            alphaTileView1.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
            alphaTileView2.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.5f)));
            alphaTileView3.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));
        });

        applyColors.setOnClickListener(v -> {
            VibrationUtils.vibrate(mainActivity, 10);
            if (hexColorValue1.getText().length() < 0 || hexColorValue1.getText().length() < 0 || hexColorValue1.getText().length() < 0) {
                ToastUtils.showCustomToast(requireActivity(), "Empty color values! Use brain...");
            } else {
                if (advancedThemes.isChecked()) {
                    String color100 = NHLPreferences.color100();
                    String color80 = hexColorValue1.getText().toString();
                    String color50 = hexColorValue2.getText().toString();
                    String color20 = hexColorValue3.getText().toString();
                    saveColor80(color100, color80, color50, color20, dynamicThemes.isChecked(), advancedThemes.isChecked());
                } else {
                    String color100 = hexColorString;
                    int color80Int = ((ColorDrawable) alphaTileView1.getBackground()).getColor();
                    int color50Int = ((ColorDrawable) alphaTileView2.getBackground()).getColor();
                    int color20Int = ((ColorDrawable) alphaTileView3.getBackground()).getColor();

                    String color80 = String.format("#%06X", (0xFFFFFF & color80Int));
                    String color50 = String.format("#%06X", (0xFFFFFF & color50Int));
                    String color20 = String.format("#%06X", (0xFFFFFF & color20Int));

                    saveColor80(color100, color80, color50, color20, dynamicThemes.isChecked(), advancedThemes.isChecked());
                }
                mainActivity.recreate(); // recreate MainActivity below
                requireActivity().recreate(); // recreate this activity AFTER to prevent closing db for stats
            }
        });

        return view;
    }

    public String adjustColorBrightness(String hexColor, float factor) {
        // https://github.com/edelstone/tints-and-shades

        int color = Color.parseColor(hexColor);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        double r_math = r * factor;
        double g_math = g * factor;
        double b_math = b * factor;
        return String.format("#%02X%02X%02X", Math.round(r_math), Math.round(g_math), Math.round(b_math));
    }

    @SuppressLint("SetTextI18n")
    private void openPickerDialog(Button button, ImageView alpha, String colorShade) {
        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());
        dialogUtils.openNhlColorPickerDialog(button, alpha, colorShade);
    }

    private void saveColor80(String color100, String color80, String color50, String color20, boolean dynamicThemeBool, boolean advThemeBool) {
        Log.d("SavedColors", "100: " + color100 + " 80: " + color80 + " 50: " + color50 + " 20: " + color20);
        // Save the color values and frame drawable to SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("customColors", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("color100", color100);
        editor.putString("color80", color80);
        editor.putString("color50", color50);
        editor.putString("color20", color20);
        editor.putBoolean("dynamicThemeBool", dynamicThemeBool);
        editor.putBoolean("advancedThemeBool", advThemeBool);
        editor.apply();
    }

}
