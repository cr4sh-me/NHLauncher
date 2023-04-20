package com.cr4sh.nhlanucher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

public class FirstSetupDialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceStateExample) {

        // Set title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.setup));
        builder.setMessage(getResources().getString(R.string.first_setup));
        setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        // Setup buttons
        builder.setPositiveButton(getResources().getString(R.string.setup), (dialog, which) -> {
            // Run setup
            dialog.cancel();
            MainUtils.run_cmd("cd /root/ && apt update && apt -y install git && [ -d NHLauncher_scripts ] && rm -rf NHLauncher_scripts ; git clone https://github.com/cr4sh-me/NHLauncher_scripts || git clone https://github.com/cr4sh-me/NHLauncher_scripts && cd NHLauncher_scripts && chmod +x * && bash nhlauncher_setup.sh && exit");
            firstSetupCompleted();
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) ->{
            dialog.cancel();
            firstSetupCompleted();
        });
        return builder.create();
    }

    private void firstSetupCompleted() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("setupSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSetupCompleted", true);
        editor.apply();
    }
}
