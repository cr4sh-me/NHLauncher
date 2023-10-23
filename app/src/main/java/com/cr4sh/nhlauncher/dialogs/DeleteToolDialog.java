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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MainUtils;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class DeleteToolDialog extends AppCompatDialogFragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_tool_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        // Get arguments
        assert getArguments() != null;
        String name = getArguments().getString("name");

        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView text1 = view.findViewById(R.id.text_view1);
        TextView text2 = view.findViewById(R.id.text_view2);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));
        text1.setTextColor(Color.parseColor(myPreferences.color80()));
        text2.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        deleteButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        deleteButton.setTextColor(Color.parseColor(myPreferences.color80()));

        text1.setText(requireActivity().getResources().getString(R.string.deleting) + name);

        deleteButton.setOnClickListener(view12 -> {
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
                    Toast.makeText(getActivity(), requireActivity().getResources().getString(R.string.cant_delete), Toast.LENGTH_SHORT).show();
                } else {
                    DBHandler.deleteTool(db, name);
                    Toast.makeText(getActivity(), requireActivity().getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                    mainUtils.restartSpinner();
                }

                // Close connection
                cursor.close();
                db.close();

            } catch (SQLiteException e) {
                // Display error
                Toast.makeText(getActivity(), "E: " + e, Toast.LENGTH_SHORT).show();
                Log.d("SQLITE", e.toString());
            }
            Objects.requireNonNull(getDialog()).cancel();
        });

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;

    }
}
