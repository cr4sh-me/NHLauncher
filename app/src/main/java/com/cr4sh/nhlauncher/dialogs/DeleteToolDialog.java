package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.Database.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.Objects;

public class DeleteToolDialog extends AppCompatDialogFragment {

    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_tool_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        NHLPreferences NHLPreferences = new NHLPreferences(requireActivity());

        // Get arguments
        assert getArguments() != null;
        String name = getArguments().getString("name");

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView text2 = view.findViewById(R.id.text_view2);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        bkg.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        title.setTextColor(Color.parseColor(NHLPreferences.color80()));
        text2.setTextColor(Color.parseColor(NHLPreferences.color50()));

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        deleteButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        deleteButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        assert name != null;
        title.setText(name.toUpperCase());

        deleteButton.setOnClickListener(view12 -> {
            VibrationUtils.vibrate(mainActivity, 10);
            // Some idiot protection
            try {
                SQLiteOpenHelper dbHandler = new DBHandler(getActivity());
                SQLiteDatabase db = dbHandler.getReadableDatabase();
                Cursor cursor = db.query("TOOLS", new String[]{"SYSTEM", "NAME"}, "NAME = ?", new String[]{name}, null, null, null, null);
                String isSystem = null;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    isSystem = cursor.getString(0);
                }

                assert isSystem != null;
                if (isSystem.equals("1")) {
                    ToastUtils.showCustomToast(requireActivity(), requireActivity().getResources().getString(R.string.cant_delete));
                } else {
                    DBHandler.deleteTool(db, name);
                    ToastUtils.showCustomToast(requireActivity(), requireActivity().getResources().getString(R.string.deleted));
                    mainUtils.restartSpinner();
                }

                // Close connection
                cursor.close();
                db.close();

            } catch (SQLiteException e) {
                // Display error
                ToastUtils.showCustomToast(requireActivity(), "E: " + e);
                Log.d("SQLITE", e.toString());
            }
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> {
            VibrationUtils.vibrate(mainActivity, 10);
            Objects.requireNonNull(getDialog()).cancel();

        });

        return view;

    }
}
