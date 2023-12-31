package com.cr4sh.nhlauncher.SettingsPager;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.StatsRecycler.StatsAdapter;
import com.cr4sh.nhlauncher.StatsRecycler.StatsItem;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment3 extends Fragment {
    private MyPreferences myPreferences;
    private RecyclerView recyclerView;
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private TextView noToolsText;
    SQLiteDatabase mDatabase;
    StatsAdapter adapter;

    public SettingsFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout3, container, false);

        myPreferences = new MyPreferences(requireActivity());
        mDatabase = mainActivity.mDatabase;

        LinearLayout spinnerBg1 = view.findViewById(R.id.spinnerBg1);
        TextView title = view.findViewById(R.id.bt_info2);
        PowerSpinnerView powerSpinnerView = view.findViewById(R.id.categories_spinner);
        noToolsText = view.findViewById(R.id.no_tools_text);
        recyclerView = view.findViewById(R.id.stats_recycler);

        adapter = new StatsAdapter();
        recyclerView.setAdapter(adapter);

        noToolsText.setTextColor(Color.parseColor(myPreferences.color80()));


        powerSpinnerView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        powerSpinnerView.setTextColor(Color.parseColor(myPreferences.color80()));
        powerSpinnerView.setHintTextColor(Color.parseColor(myPreferences.color50()));
        powerSpinnerView.setDividerColor(Color.parseColor(myPreferences.color80()));

        title.setTextColor(Color.parseColor(myPreferences.color80()));

        powerSpinnerView.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            spinnerChanger(newIndex);
        });

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(8, Color.parseColor(myPreferences.color50())); // Stroke width and color
        gd.setCornerRadius(20);
        spinnerBg1.setBackground(gd);


        myPreferences = new MyPreferences(requireActivity());


        return view;
    }

    @SuppressLint({"SetTextI18n", "Recycle"})
    public void spinnerChanger(int category) {

        // Obtain references to app resources and button layout
        Resources resources = mainActivity.getResources();

        Cursor cursor;

        String[] projection = {"CATEGORY", "FAVOURITE", "NAME", "ICON", "USAGE"};
        String selection;
        String[] selectionArgs;

        if (category == 0) {
            selection = "USAGE > ?";
            selectionArgs = new String[]{"0"};
        } else if (category == 1) {
            selection = "FAVOURITE = ? AND USAGE > ?";
            selectionArgs = new String[]{"1", "0"};
         } else {
            selection = "CATEGORY = ? AND USAGE > ?";
            selectionArgs = new String[]{String.valueOf(category-1), "0"};
        }

        cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, myPreferences.sortingMode(), null);
        if (cursor.getCount() == 0) {
            requireActivity().runOnUiThread(() -> {
                noToolsText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            });
        } else {
            mainActivity.runOnUiThread(() -> {
                noToolsText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            });

            // Create a new itemList from the cursor data
            List<StatsItem> newItemList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String toolName = cursor.getString(2);
                String toolIcon = cursor.getString(3);
                int toolUsage = cursor.getInt(4);
                String toolUsageString = String.valueOf(toolUsage);

                StatsItem item = new StatsItem(toolName, toolIcon, toolUsageString);
                newItemList.add(item);

            }
            Log.d("recview", newItemList.toString());
            mainActivity.runOnUiThread(() -> {
                    adapter.updateData(newItemList);
            });
        }
        cursor.close();

    }

    private void setContainerBackground(LinearLayout container, String color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(color));
        container.setBackground(drawable);
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        button.setTextColor(Color.parseColor(myPreferences.color80()));
    }
}
