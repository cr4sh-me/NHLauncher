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
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.NHLPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.VibrationUtil;
import com.flask.colorpicker.ColorPickerView;

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

        NHLPreferences NHLPreferences = new NHLPreferences(requireActivity());

        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        ColorPickerView colorPickerView = view.findViewById(R.id.color_picker_view);

        EditText hexColorValue = view.findViewById(R.id.customHexColor);

        Button applyColors = view.findViewById(R.id.apply_custom_colors);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        hexColorValue.setTextColor(Color.parseColor(NHLPreferences.color80()));
        hexColorValue.getBackground().mutate().setTint(Color.parseColor(NHLPreferences.color50()));

        applyColors.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        applyColors.setTextColor(Color.parseColor(NHLPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        colorPickerView.setInitialColor(Color.parseColor(hexColorShade), true);

        hexColorValue.setText(button.getText());
        colorPickerView.addOnColorChangedListener(selectedColor -> {
            hexColorString = (("#" + Integer.toHexString(selectedColor)).toUpperCase());
            hexColorValue.setText(hexColorString);
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
