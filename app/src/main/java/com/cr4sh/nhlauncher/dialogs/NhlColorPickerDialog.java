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

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.VibrationUtil;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.Objects;


public class NhlColorPickerDialog extends AppCompatDialogFragment {

    private final Button button;
    private final ImageView alpha;
    private final String hexColorShade;
    private String hexColorString;
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();

    public NhlColorPickerDialog(Button button, ImageView alpha, String hexColorShade) {
        this.button = button;
        this.alpha = alpha;
        this.hexColorShade = hexColorShade;
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_dialog, container, false);

        MyPreferences myPreferences = new MyPreferences(requireActivity());

        TextView title = view.findViewById(R.id.dialog_title);
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

        colorPickerView.setInitialColor(Color.parseColor(hexColorShade));
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        BubbleFlag at = new BubbleFlag(requireActivity());
        at.setFlagMode(FlagMode.ALWAYS);
        colorPickerView.setFlagView(at);

        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            ColorEnvelope colorEnvelope = new ColorEnvelope(color);
            hexColorString = "#" + colorEnvelope.getHexCode();
        });

        applyColors.setOnClickListener(v -> {
            VibrationUtil.vibrate(mainActivity, 10);
            alpha.setBackgroundColor(Color.parseColor(hexColorString));
            button.setText(hexColorString);
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> {
            VibrationUtil.vibrate(mainActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();
        });

        return view;

    }
}
