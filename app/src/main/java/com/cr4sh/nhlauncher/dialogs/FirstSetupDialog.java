package com.cr4sh.nhlauncher.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.Objects;

public class FirstSetupDialog extends AppCompatDialogFragment {

    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_setup_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        NHLPreferences NHLPreferences = new NHLPreferences(requireActivity());

        setCancelable(false);

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView text = view.findViewById(R.id.text_view1);
        Button setupButton = view.findViewById(R.id.setup_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));
        text.setTextColor(Color.parseColor(NHLPreferences.color80()));

        setupButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        setupButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));


        setupButton.setOnClickListener(view12 -> {
            VibrationUtils.vibrate(mainActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();
            mainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit");
            firstSetupCompleted();
        });

        cancelButton.setOnClickListener(view12 -> {
            VibrationUtils.vibrate(mainActivity, 10);
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
