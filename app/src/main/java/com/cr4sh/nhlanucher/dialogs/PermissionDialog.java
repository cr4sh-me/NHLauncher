package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.DBHandler;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

//public class PermissionDialog extends DialogFragment {
//
//    @NonNull
//    public Dialog onCreateDialog(Bundle savedInstanceStateExample) {
//
//
//        // Set title and message
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        builder.setTitle(getResources().getString(R.string.all_file_perm));
//        builder.setMessage(getResources().getString(R.string.perm_dialog));
//        builder.setIcon(android.R.drawable.ic_dialog_alert);
//        setCancelable(false);
//        builder.setPositiveButton(getResources().getString(R.string.allow), (dialogInterface, i) -> MainUtils.takePermissions());
//        return builder.create();
//    }
//}

public class PermissionDialog extends AppCompatDialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permissions_dialog, container, false);

        setCancelable(false);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Button allowButton = view.findViewById(R.id.allow_button);

        view.setBackgroundResource(frame);
        allowButton.setTextColor(Color.parseColor(nameColor));

        allowButton.setOnClickListener(view12 -> {
            Objects.requireNonNull(getDialog()).cancel();
            MainUtils.takePermissions();
        });

        return view;

    }
}
