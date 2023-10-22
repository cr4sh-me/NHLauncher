package com.cr4sh.nhlauncher.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MainUtils;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class FirstSetupDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_setup_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());

        setCancelable(false);

        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        setupButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit");
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
