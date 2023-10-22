package com.cr4sh.nhlauncher.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.PermissionUtils;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class PermissionDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_dialog, container, false);

        setCancelable(false);
        Button allowButton = view.findViewById(R.id.allow_button);

        allowButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            PermissionUtils permissionUtils = new PermissionUtils((MainActivity) requireActivity());
            permissionUtils.takePermissions();
        });

        return view;

    }
}
