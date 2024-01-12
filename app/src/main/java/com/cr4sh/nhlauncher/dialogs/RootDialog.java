package com.cr4sh.nhlauncher.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlauncher.R;

public class RootDialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceStateExample) {

        // Set title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(getResources().getString(R.string.root_error));
        builder.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
            dialog.cancel();
            requireActivity().finish();
        });
        return builder.create();
    }
}