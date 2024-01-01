package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.WPSAttack;
import com.cr4sh.nhlauncher.utils.ToastUtils;

import java.util.Objects;

public class WpsCustomPinDialog extends AppCompatDialogFragment {

    WPSAttack wpsAttack;

    public WpsCustomPinDialog(WPSAttack activity) {
        this.wpsAttack = activity;
    }


    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wps_custom_pin_dialog, container, false);

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        assert getArguments() != null;
        int option = getArguments().getInt("option");

        setCancelable(false);

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        EditText customPin = view.findViewById(R.id.customPin);

        if (option == 1) {
            cancelButton.setText("Don't use custom PIN");
            title.setText("Custom PIN");
            customPin.setHint("Custom PIN");
            if (!wpsAttack.customPINCMD.isEmpty()) {
                String customPINCMD = wpsAttack.customPINCMD;
                customPin.setText(removePrefix(customPINCMD, " -p "));
            }
        } else {
            cancelButton.setText("Don't use custom delay");
            title.setText("Custom delay");
            customPin.setHint("Custom delay");
            if (!wpsAttack.delayCMD.isEmpty()) {
                String delayCMD = wpsAttack.delayCMD;
                customPin.setText(removePrefix(delayCMD, " -d "));
            }
        }

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        customPin.setTextColor(Color.parseColor(myPreferences.color80()));
        customPin.getBackground().mutate().setTint(Color.parseColor(myPreferences.color50()));
        customPin.setHintTextColor(Color.parseColor(myPreferences.color50()));

        setupButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        setupButton.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));


        setupButton.setOnClickListener(view12 -> {
            if (customPin.getText().toString().isEmpty()) {
                ToastUtils.showCustomToast(requireActivity(),"Empty input!");
            } else {
                Objects.requireNonNull(getDialog()).cancel();
                if (option == 1) {
                    wpsAttack.customPINCMD = " -p " + customPin.getText().toString();
                } else {
                    wpsAttack.delayCMD = " -d " + customPin.getText().toString();
                }
            }
        });

        cancelButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            if (option == 1) {
                wpsAttack.customPINCMD = "";
            } else {
                wpsAttack.delayCMD = "";
            }
        });

        return view;

    }

    private String removePrefix(String input, String prefix) {
        // Check if the input starts with the specified prefix
        if (input.startsWith(prefix)) {
            // Remove the prefix from the input
            return input.substring(prefix.length());
        }
        // If the input doesn't start with the prefix, return the original string
        return input;
    }

}
