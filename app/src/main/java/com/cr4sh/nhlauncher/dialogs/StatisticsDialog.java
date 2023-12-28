package com.cr4sh.nhlauncher.dialogs;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.cr4sh.nhlauncher.CustomSpinnerAdapter;
import com.cr4sh.nhlauncher.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StatisticsDialog extends DialogFragment {
    MainActivity myActivity;

    public StatisticsDialog(MainActivity activity) {
        this.myActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.statistics_dialog, container, false);

//        MainUtils mainUtils = new MainUtils((MainActivity) requireActivity());
        MyPreferences myPreferences = new MyPreferences(requireActivity());


        TextView title = view.findViewById(R.id.dialog_title);
        LinearLayout bkg = view.findViewById(R.id.custom_theme_dialog_background);
        Spinner statsSpinner = view.findViewById(R.id.stats_spinner);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        LinearLayout layout = view.findViewById(R.id.stats_layout);

        // Apply custom themes
        bkg.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        title.setTextColor(Color.parseColor(myPreferences.color80()));

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        List<String> valuesList = Arrays.asList(
                getResources().getString(R.string.all_categories),
                getResources().getString(R.string.category_ft),
                getResources().getString(R.string.category_01),
                getResources().getString(R.string.category_02),
                getResources().getString(R.string.category_03),
                getResources().getString(R.string.category_04),
                getResources().getString(R.string.category_05),
                getResources().getString(R.string.category_06),
                getResources().getString(R.string.category_07),
                getResources().getString(R.string.category_08),
                getResources().getString(R.string.category_09),
                getResources().getString(R.string.category_10),
                getResources().getString(R.string.category_11),
                getResources().getString(R.string.category_13)
        );
        List<Integer> imageList = Arrays.asList(
                R.drawable.nhl_all_trans,
                R.drawable.nhl_favourite_trans,
                R.drawable.kali_info_gathering_trans,
                R.drawable.kali_vuln_assessment_trans,
                R.drawable.kali_web_application_trans,
                R.drawable.kali_database_assessment_trans,
                R.drawable.kali_password_attacks_trans,
                R.drawable.kali_wireless_attacks_trans,
                R.drawable.kali_reverse_engineering_trans,
                R.drawable.kali_exploitation_tools_trans,
                R.drawable.kali_sniffing_spoofing_trans,
                R.drawable.kali_maintaining_access_trans,
                R.drawable.kali_forensics_trans,
                R.drawable.kali_reporting_tools_trans,
                R.drawable.kali_social_engineering_trans
        );

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(myActivity, valuesList, imageList, myPreferences.color20(), myPreferences.color80());
        statsSpinner.setAdapter(customSpinnerAdapter);

        GradientDrawable drawableToolbar = new GradientDrawable();
        drawableToolbar.setCornerRadius(100);
        drawableToolbar.setStroke(8, Color.parseColor(myPreferences.color50()));
        statsSpinner.setBackground(drawableToolbar);

        Animation anim = AnimationUtils.loadAnimation(myActivity, R.anim.fade_in);

//        ArrayAdapter<String> defaultAdapter = new ArrayAdapter<>(myActivity, android.R.layout.simple_spinner_item, myActivity.valuesList);
//        defaultAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        statsSpinner.setAdapter(defaultAdapter);


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
                        } else if (category.contains("AC -")) {
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
                            noToolsTextView.startAnimation(anim);
                            noToolsTextView.setText(getResources().getString(R.string.no_tools_stat));
                            noToolsTextView.setGravity(Gravity.CENTER);
                            noToolsTextView.setTextColor(Color.parseColor(myPreferences.color80()));
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
                                nameTextView.setTextColor(Color.parseColor(myPreferences.color80()));
                                nameTextView.setTypeface(null, Typeface.BOLD);
                                nameTextView.setGravity(Gravity.CENTER);

                                // Add the TextView to the LinearLayout
                                toolLayout.addView(nameTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                                // Create a new TextView element for the tool usage count
                                TextView countTextView = new TextView(requireActivity());
                                countTextView.setTypeface(null, Typeface.BOLD);
                                countTextView.setText(String.format(getResources().getString(R.string.usage) + "%d", toolUsage));
                                countTextView.setTextColor(Color.parseColor(myPreferences.color80()));
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