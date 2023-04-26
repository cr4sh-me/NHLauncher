package com.cr4sh.nhlanucher.dialogs;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlanucher.DBHandler;
import com.cr4sh.nhlanucher.MainActivity;
import com.cr4sh.nhlanucher.MainUtils;
import com.cr4sh.nhlanucher.MyPreferences;
import com.cr4sh.nhlanucher.R;

import java.util.Objects;

public class StatisticsDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.statistics_dialog, container, false);

        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());

        String frameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        String nameColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        String descColor = requireActivity().getSharedPreferences("customColors", MODE_PRIVATE).getString("descriptionColor", "#FFFFFF");
        @SuppressLint("DiscouragedApi") int frame = requireActivity().getResources().getIdentifier(frameColor, "drawable", requireActivity().getPackageName());

        Spinner statsSpinner = view.findViewById(R.id.stats_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        LinearLayout layout = view.findViewById(R.id.stats_layout);

        view.setBackgroundResource(frame);
        cancelButton.setTextColor(Color.parseColor(nameColor));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statsSpinner.setAdapter(adapter);

        statsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view2, int i, long l) {

                layout.removeAllViews();

                String category = adapterView.getItemAtPosition(i).toString();

                new Thread(() -> {
                    Cursor cursor = null;
                    try (SQLiteOpenHelper dbHandler = new DBHandler(requireActivity());
                         SQLiteDatabase db = dbHandler.getReadableDatabase()) {

                        String[] projection = {"SYSTEM", "CATEGORY", "FAVOURITE", "NAME", myPreferences.language(), "CMD", "ICON", "USAGE"};
                        String selection;
                        String[] selectionArgs;

                        if (category.contains("FT")) {
                            selection = "FAVOURITE = ? AND USAGE > ?";
                            selectionArgs = new String[]{"1", "0"};
                        } else if (category.contains("AL -")) {
                            selection = "USAGE > ?";
                            selectionArgs = new String[]{"0"};
                        } else {
                            String category_number = category.substring(0, 2);
                            selection = "CATEGORY = ? AND USAGE > ?";
                            selectionArgs = new String[]{category_number, "0"};
                        }

                        cursor = db.query("TOOLS", projection, selection, selectionArgs, null, null, "USAGE DESC", null);

                        if (cursor.getCount() == 0) {
                            // If there are no tools, display a "NO TOOLS" message in the center of the layout
                            TextView noToolsTextView = new TextView(requireActivity());
                            noToolsTextView.setText(getResources().getString(R.string.no_tools_stat));
                            noToolsTextView.setTextColor(Color.parseColor(nameColor));
                            noToolsTextView.setGravity(Gravity.CENTER);
                            noToolsTextView.setTextSize(16);
                            noToolsTextView.setTypeface(null, Typeface.BOLD);
                            requireActivity().runOnUiThread(() -> layout.addView(noToolsTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
                        } else {
                            // If there are no favorites, display a message in the center of the layout
                            while (cursor.moveToNext()) {
                                String toolName = cursor.getString(3);
                                String toolIcon = cursor.getString(6);
                                int toolUsage = cursor.getInt(7);

                                // Create a new LinearLayout to hold all elements for the tool
                                LinearLayout toolLayout = new LinearLayout(requireActivity());
                                toolLayout.setOrientation(LinearLayout.HORIZONTAL);
                                toolLayout.setPadding(16, 16, 16, 16);
                                toolLayout.setGravity(Gravity.CENTER_VERTICAL);

                                // Create a new ImageView for the tool icon
                                ImageView imageView = new ImageView(requireActivity());
                                @SuppressLint("DiscouragedApi") int icon = requireActivity().getResources().getIdentifier(toolIcon, "drawable", requireActivity().getPackageName());
                                Drawable image = ResourcesCompat.getDrawable(requireActivity().getResources(), icon, null);
                                imageView.setImageDrawable(image);
                                imageView.setPadding(0, 0, 16, 0);

                                // Add the ImageView to the LinearLayout
                                toolLayout.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

                                // Create a new TextView element for the tool name
                                TextView nameTextView = new TextView(requireActivity());
                                nameTextView.setText(toolName.toUpperCase());
                                nameTextView.setTextColor(Color.parseColor(nameColor));
                                nameTextView.setTextSize(16);
                                nameTextView.setTypeface(null, Typeface.BOLD);
                                nameTextView.setGravity(Gravity.CENTER);

                                // Add the TextView to the LinearLayout
                                toolLayout.addView(nameTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                                // Create a new TextView element for the tool usage count
                                TextView countTextView = new TextView(requireActivity());
                                countTextView.setTextColor(Color.parseColor(descColor));
                                countTextView.setTextSize(16);
                                countTextView.setTypeface(null, Typeface.BOLD);
                                countTextView.setText(String.format(getResources().getString(R.string.usage) + "%d", toolUsage));
                                countTextView.setGravity(Gravity.CENTER_VERTICAL);
                                countTextView.setPadding(0, 0, 0, 0);

                                // Add the TextView to the LinearLayout
                                toolLayout.addView(countTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));

                                // Add the LinearLayout to the layout
                                requireActivity().runOnUiThread(() -> layout.addView(toolLayout));
                            }
                        }

                    } catch (SQLiteException e) {
                        // Display error
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error: " + e, Toast.LENGTH_SHORT).show());
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancelButton.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).cancel());

        return view;
    }

}