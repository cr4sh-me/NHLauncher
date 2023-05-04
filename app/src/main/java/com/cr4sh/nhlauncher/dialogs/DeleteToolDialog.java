package com.cr4sh.nhlauncher.dialogs;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.cr4sh.nhlauncher.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MainUtils;
import com.cr4sh.nhlauncher.R;

import java.util.Objects;

public class DeleteToolDialog extends AppCompatDialogFragment {

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_tool_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());

        // Get arguments
        assert getArguments() != null;
        String name = getArguments().getString("name");

        TextView text1 = view.findViewById(R.id.text_view1);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        text1.setText(requireActivity().getResources().getString(R.string.deleting) + name);

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        view.setBackgroundResource(frame);
        cancelButton.setTextColor(Color.parseColor(nameColor));
        deleteButton.setTextColor(Color.parseColor(nameColor));

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
                }

                mainUtils.restartSpinner();
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
