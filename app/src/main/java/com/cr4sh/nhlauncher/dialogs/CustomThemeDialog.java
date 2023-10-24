package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.widget.CompoundButtonCompat;

import com.cr4sh.nhlauncher.DialogUtils;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.util.Objects;


public class CustomThemeDialog extends AppCompatDialogFragment {

//    MainActivity myActivity;
    String hexColorString;

    MyPreferences myPreferences;

    //    public CustomThemeDialog(MainActivity activity) {
//        this.myActivity = activity;
//    }
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_theme_dialog, container, false);

//        MainUtils mainUtils = new MainUtils(myActivity);
        myPreferences = new MyPreferences(requireActivity());

        LinearLayout manualBox = view.findViewById(R.id.hiddenLayout);
        LinearLayout advancedMode = view.findViewById(R.id.advancedLayout);
        RelativeLayout alphaLayout = view.findViewById(R.id.alphaLayout);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView text2  = view.findViewById(R.id.text_second);
        ScrollView bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
//        ImageView alphaTileView1 = view.findViewById(R.id.alphaTileView1);
        ImageView alphaTileView2 = view.findViewById(R.id.alphaTileView2);
        ImageView alphaTileView3 = view.findViewById(R.id.alphaTileView3);
        ImageView alphaTileView4 = view.findViewById(R.id.alphaTileView4);
        ImageView aalphaTileView1 = view.findViewById(R.id.advancedAlphaTileView1);
        ImageView aalphaTileView2 = view.findViewById(R.id.advancedAlphaTileView2);
        ImageView aalphaTileView3 = view.findViewById(R.id.advancedAlphaTileView3);
        Button hexColorValue1 = view.findViewById(R.id.customHexColor1);
        Button hexColorValue2 = view.findViewById(R.id.customHexColor2);
        Button hexColorValue3 = view.findViewById(R.id.customHexColor3);
        EditText hexColorValue = view.findViewById(R.id.customHexColor);
        CheckBox dynamicThemes = view.findViewById(R.id.dynamic_themes_checkbox);
        CheckBox advancedThemes = view.findViewById(R.id.advanced_themes_checkbox);
        Button applyColors = view.findViewById(R.id.apply_custom_colors);
        Button cancelButton = view.findViewById(R.id.cancel_button);


        GradientDrawable imgBorder = new GradientDrawable();
        GradientDrawable imgBorder2 = new GradientDrawable();
        GradientDrawable imgBorder3 = new GradientDrawable();
        GradientDrawable imgBorder4 = new GradientDrawable();
        GradientDrawable imgBorder5 = new GradientDrawable();
        GradientDrawable imgBorder6 = new GradientDrawable();
        GradientDrawable imgBorder7 = new GradientDrawable();

        hexColorString = myPreferences.color100();



        // Checkbox set

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            dynamicThemes.setChecked(myPreferences.dynamicThemeBool());
        } else {
            dynamicThemes.setEnabled(false);
        }

        if(dynamicThemes.isChecked()){
            manualBox.setVisibility(View.GONE);
        } else {
            manualBox.setVisibility(View.VISIBLE);
            colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
        }

        if(myPreferences.advancedThemeBool()){
            advancedThemes.setChecked(true);
            advancedMode.setVisibility(View.VISIBLE);
            alphaLayout.setVisibility(View.GONE);
            hexColorValue.setVisibility(View.GONE);
            colorPickerView.setVisibility(View.GONE);
            hexColorValue1.setText(myPreferences.color80());
            hexColorValue2.setText(myPreferences.color50());
            hexColorValue3.setText(myPreferences.color20());

            aalphaTileView1.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
            aalphaTileView2.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.5f)));
            aalphaTileView3.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));


        } else {
            advancedThemes.setChecked(false);
            advancedMode.setVisibility(View.GONE);
            alphaLayout.setVisibility(View.VISIBLE);
            hexColorValue.setVisibility(View.VISIBLE);
            colorPickerView.setVisibility(View.VISIBLE);
//            colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
        }

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));
        text2.setTextColor(Color.parseColor(myPreferences.color80()));
        dynamicThemes.setTextColor(Color.parseColor(myPreferences.color80()));
        advancedThemes.setTextColor(Color.parseColor(myPreferences.color80()));

        int [][] states = {{android.R.attr.state_checked}, {}};
        int [] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(dynamicThemes, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(advancedThemes, new ColorStateList(states, colors));

        hexColorValue.setTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        hexColorValue1.setTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue1.setHintTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue1.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        hexColorValue2.setTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue2.setHintTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue2.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        hexColorValue3.setTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue3.setHintTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue3.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));

        applyColors.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        applyColors.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        hexColorValue.setEnabled(false);

        BubbleFlag at = new BubbleFlag(requireActivity());
        at.setFlagMode(FlagMode.ALWAYS);
        colorPickerView.setFlagView(at);



//        colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.kali_hashid))

        dynamicThemes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                manualBox.setVisibility(View.GONE);
            } else {
                manualBox.setVisibility(View.VISIBLE);
                colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
            }
        });

        advancedThemes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                advancedMode.setVisibility(View.VISIBLE);
                alphaLayout.setVisibility(View.GONE);
                hexColorValue.setVisibility(View.GONE);
                colorPickerView.setVisibility(View.GONE);
                hexColorValue1.setText(myPreferences.color80());
                hexColorValue2.setText(myPreferences.color50());
                hexColorValue3.setText(myPreferences.color20());

                aalphaTileView1.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
                aalphaTileView2.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.5f)));
                aalphaTileView3.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));

            } else {
                advancedMode.setVisibility(View.GONE);
                alphaLayout.setVisibility(View.VISIBLE);
                hexColorValue.setVisibility(View.VISIBLE);
                colorPickerView.setVisibility(View.VISIBLE);
                colorPickerView.setInitialColor(Color.parseColor(myPreferences.color100()));
            }
        });

        hexColorValue1.setOnClickListener(v -> openPickerDialog(hexColorValue1, aalphaTileView1));
        hexColorValue2.setOnClickListener(v -> openPickerDialog(hexColorValue2, aalphaTileView2));
        hexColorValue3.setOnClickListener(v -> openPickerDialog(hexColorValue3, aalphaTileView3));


        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            bkg.requestDisallowInterceptTouchEvent(true);
            ColorEnvelope colorEnvelope = new ColorEnvelope(color);
            hexColorValue.setText("#" + colorEnvelope.getHexCode());
            hexColorString = "#" + colorEnvelope.getHexCode();

            alphaLayout.setBackgroundColor(color);

            alphaTileView2.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
            alphaTileView3.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.5f)));
            alphaTileView4.setBackgroundColor(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));

//            alphaTileView1.setBackgroundColor(color);
//            alphaTileView2.setColorFilter(Color.parseColor(adjustColorBrightness(hexColorString, 0.8f)));
//            alphaTileView3.setColorFilter(Color.parseColor(adjustColorBrightness(hexColorString, 0.6f)));
//            alphaTileView4.setColorFilter(Color.parseColor(adjustColorBrightness(hexColorString, 0.2f)));
        });
        
        applyColors.setOnClickListener(v -> {

            if(hexColorValue1.getText().length() < 0 || hexColorValue1.getText().length() < 0 || hexColorValue1.getText().length() < 0){
                Toast.makeText(requireActivity(), "Empty color values! Use brain...", Toast.LENGTH_SHORT).show();
            } else {
                if(advancedThemes.isChecked()){
                    String color100 = hexColorValue1.getText().toString();
                    String color80 = hexColorValue1.getText().toString();
                    String color50 = hexColorValue2.getText().toString();
                    String color20 = hexColorValue3.getText().toString();
                    saveColor80(color100, color80, color50, color20, dynamicThemes.isChecked(), advancedThemes.isChecked());
                } else {
                    String color100 = hexColorString;
                    String color80 = adjustColorBrightness(hexColorString, 0.8f);
                    String color50 = adjustColorBrightness(hexColorString, 0.5f);
                    String color20 = adjustColorBrightness(hexColorString, 0.2f);
                    saveColor80(color100, color80, color50, color20, dynamicThemes.isChecked(), advancedThemes.isChecked());
                }
                requireActivity().recreate();
            }
        });
        
        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }

    @SuppressLint("SetTextI18n")
    private void openPickerDialog(Button button, ImageView alpha) {

        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());
        dialogUtils.openNhlColorPickerDialog(button, alpha);
    }

    public static String adjustColorBrightness(String hexColor, float factor) {
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



    private void saveColor80(String color100, String color80, String color50, String color20, boolean dynamicThemeBool, boolean advThemeBool) {
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
