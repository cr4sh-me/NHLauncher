package com.cr4sh.nhlauncher.SettingsPager;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.StatsRecycler.StatsAdapter;
import com.cr4sh.nhlauncher.StatsRecycler.StatsItem;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SettingsFragment3 extends Fragment {
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    SQLiteDatabase mDatabase;
    StatsAdapter adapter;
    private NHLPreferences NHLPreferences;
    private RecyclerView recyclerView;
    private TextView noToolsText;
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();

    public SettingsFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout3, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());

        mDatabase = mainActivity.mDatabase;


        LinearLayout spinnerBg1 = view.findViewById(R.id.spinnerBg1);
        TextView title = view.findViewById(R.id.bt_info2);
        PowerSpinnerView powerSpinnerView = view.findViewById(R.id.categories_spinner);
        noToolsText = view.findViewById(R.id.no_tools_text);
        recyclerView = view.findViewById(R.id.stats_recycler);

        adapter = new StatsAdapter();
        recyclerView.setAdapter(adapter);

        noToolsText.setTextColor(Color.parseColor(NHLPreferences.color80()));


        powerSpinnerView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        powerSpinnerView.setTextColor(Color.parseColor(NHLPreferences.color80()));
        powerSpinnerView.setHintTextColor(Color.parseColor(NHLPreferences.color50()));
        powerSpinnerView.setDividerColor(Color.parseColor(NHLPreferences.color80()));

        title.setTextColor(Color.parseColor(NHLPreferences.color80()));

        powerSpinnerView.selectItemByIndex(0);
        spinnerChanger(0); // Display all tools by default

        powerSpinnerView.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> spinnerChanger(newIndex));

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(8, Color.parseColor(NHLPreferences.color50())); // Stroke width and color
        gd.setCornerRadius(20);
        spinnerBg1.setBackground(gd);

        powerSpinnerView.setSpinnerOutsideTouchListener((view1, motionEvent) -> powerSpinnerView.selectItemByIndex(powerSpinnerView.getSelectedIndex()));

        return view;
    }

    @SuppressLint({"SetTextI18n", "Recycle"})
    public void spinnerChanger(int category) {
        Future<?> future = executor.submit(() -> {
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
                selectionArgs = new String[]{String.valueOf(category - 1), "0"};
            }

            cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, NHLPreferences.sortingMode(), null);
            if (cursor.getCount() == 0) {
                mainActivity.runOnUiThread(() -> {
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

                mainActivity.runOnUiThread(() -> adapter.updateData(newItemList));
            }
            cursor.close();
        });

        try {
            future.get(); // This will wait for the background task to complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
