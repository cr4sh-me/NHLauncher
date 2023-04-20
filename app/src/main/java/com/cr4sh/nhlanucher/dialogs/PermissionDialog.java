package com.cr4sh.nhlanucher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

public class PermissionDialog extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceStateExample) {


        // Set title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getResources().getString(R.string.all_file_perm));
        builder.setMessage(getResources().getString(R.string.perm_dialog));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.allow), (dialogInterface, i) -> MainUtils.takePermissions());
        return builder.create();
    }
}