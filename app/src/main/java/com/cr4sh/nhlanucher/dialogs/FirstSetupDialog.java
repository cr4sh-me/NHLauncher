package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class FirstSetupDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_setup_dialog, container, false);

        setCancelable(false);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        view.setBackgroundResource(frame);
        setupButton.setTextColor(Color.parseColor(nameColor));
        cancelButton.setTextColor(Color.parseColor(nameColor));

        setupButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            MainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit");
            firstSetupCompleted();
        });

        cancelButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            firstSetupCompleted();
        });

        return view;

    }
    private void firstSetupCompleted() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("setupSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSetupCompleted", true);
        editor.apply();
    }
}
