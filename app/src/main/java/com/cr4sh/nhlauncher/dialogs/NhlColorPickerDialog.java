package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.Objects;


public class NhlColorPickerDialog extends AppCompatDialogFragment {

    Button button;
    ImageView alpha;
        public NhlColorPickerDialog(Button button, ImageView alpha) {
        this.button = button;
        this.alpha = alpha;
    }

    //    MainActivity myActivity;
    String hexColorString;

    MyPreferences myPreferences;

    //    public CustomThemeDialog(MainActivity activity) {
//        this.myActivity = activity;
//    }
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_dialog, container, false);

//        MainUtils mainUtils = new MainUtils(myActivity);
        myPreferences = new MyPreferences(requireActivity());

//        LinearLayout manualBox = view.findViewById(R.id.hiddenLayout);
//        LinearLayout advancedMode = view.findViewById(R.id.advancedLayout);
//        RelativeLayout alphaLayout = view.findViewById(R.id.alphaLayout);
        TextView title = view.findViewById(R.id.dialog_title);
//        TextView text2  = view.findViewById(R.id.text_second);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
        BrightnessSlideBar brightnessSlideBar = view.findViewById(R.id.brightnessBar);

        EditText hexColorValue = view.findViewById(R.id.customHexColor);

        Button applyColors = view.findViewById(R.id.apply_custom_colors);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Checkbox set

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        hexColorValue.setTextColor(Color.parseColor(myPreferences.color80()));
        hexColorValue.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));

        applyColors.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        applyColors.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        colorPickerView.setInitialColor(Color.parseColor(button.getText().toString()));

        BubbleFlag at = new BubbleFlag(requireActivity());
        at.setFlagMode(FlagMode.ALWAYS);
        colorPickerView.setFlagView(at);

        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            ColorEnvelope colorEnvelope = new ColorEnvelope(color);
            hexColorValue.setText("#" + colorEnvelope.getHexCode());
            hexColorString = "#" + colorEnvelope.getHexCode();
            alpha.setBackgroundColor(Color.parseColor(hexColorString));

        });

        applyColors.setOnClickListener(v -> {
            button.setText(hexColorString);
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }
}
