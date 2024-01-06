package com.cr4sh.nhlauncher.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.ButtonsRecycler.NHLAdapter;
import com.cr4sh.nhlauncher.ButtonsRecycler.NHLItem;
import com.cr4sh.nhlauncher.Database.DBHandler;
import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.bridge.Bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainUtils extends AppCompatActivity {

    private final SQLiteDatabase mDatabase;
    private final MainActivity mainActivity;
    private final MyPreferences myPreferences;

    public MainUtils(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mDatabase = mainActivity.mDatabase;
        myPreferences = new MyPreferences(mainActivity);
    }

    // MainUtils functions!!!

    // NetHunter bridge function
    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, true);
//        intent.putExtra(EXTRA_FOREGOUND, true)
        mainActivity.startActivity(intent);
    }

    // Increase button usage by 1
    public void buttonUsageIncrease(String name) {
        DBHandler.updateToolUsage(mDatabase, name, mainActivity.buttonUsage + 1);
        restartSpinner();
    }

    // Queries db for buttons with given categories and display them!
    @SuppressLint({"SetTextI18n", "Recycle"})
    public void spinnerChanger(int category) {

        mainActivity.changeCategoryPreview(category); // Set category preview

        // Obtain references to app resources and button layout
        Resources resources = mainActivity.getResources();
        RecyclerView layout = mainActivity.recyclerView;
        TextView noToolsText = mainActivity.findViewById(R.id.messagebox);
        noToolsText.setText(null);

        String[] projection = {"CATEGORY", "FAVOURITE", "NAME", myPreferences.language(), "CMD", "ICON", "USAGE"};
        String selection;
        String[] selectionArgs;

        if (category == 0) {
            selection = "FAVOURITE = ?";
            selectionArgs = new String[]{"1"};
            // Disable creating new buttons in fav category
            MainActivity.disableMenu = true;
        } else {
            String category_number = String.valueOf(category);
            selection = "CATEGORY = ?";
            selectionArgs = new String[]{category_number};
            // Enable creating new buttons in normal categories
            MainActivity.disableMenu = false;
        }

        if (mainActivity.executor.isTerminated() || mainActivity.executor.isShutdown()) {
            mainActivity.executor = Executors.newCachedThreadPool();
        }

        Future<List<NHLItem>> queryTask = mainActivity.executor.submit(() -> {
            Cursor cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, myPreferences.sortingMode(), null);

            List<NHLItem> newItemList = new ArrayList<>();

            if (cursor.getCount() > 0) {
                // Create a new itemList from the cursor data
                while (cursor.moveToNext()) {
                    String toolCategory = cursor.getString(0);
                    String toolName = cursor.getString(2);
                    String toolDescription = cursor.getString(3);
                    String toolCmd = cursor.getString(4);
                    String toolIcon = cursor.getString(5);
                    int toolUsage = cursor.getInt(6);

//                    Log.d("MainUtilsLog", toolCategory + toolCategory + toolCmd);

                    NHLItem item = new NHLItem(toolCategory, toolName, toolDescription, toolCmd, toolIcon, toolUsage);
                    newItemList.add(item);
                }
            }
            cursor.close();
            return newItemList;
        });

        mainActivity.runOnUiThread(() -> {
            try {
                List<NHLItem> newItemList = queryTask.get(); // This blocks until the task is done
                if (newItemList.isEmpty()) {
                    RecyclerView.Adapter<?> adapter = layout.getAdapter();
                    if (adapter instanceof NHLAdapter) {
                        ((NHLAdapter) adapter).updateData(new ArrayList<>()); // Empty list to clear existing data
                    }
//                    mainActivity.disableWhileAnimation(layout);
                    mainActivity.enableAfterAnimation(noToolsText);
                    noToolsText.setText(resources.getString(R.string.no_fav_tools));
                } else {
                    // layout.scrollToPosition(0); // Scroll to first tool
                    noToolsText.setText(null);
                    mainActivity.disableWhileAnimation(noToolsText);

                    RecyclerView.Adapter<?> adapter = layout.getAdapter();
                    if (adapter instanceof NHLAdapter) {
                        ((NHLAdapter) adapter).updateData(newItemList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    // Fills our spinner with text and images
    public void restartSpinner() {
        spinnerChanger(mainActivity.currentCategoryNumber); // Just set to category chosen before
    }

    // Refreshes our TextView that is responsible for app background

    // Adds button to favourites bu updating FAVOURITE value
    public void addFavourite() {

        Cursor cursor = mDatabase.query("TOOLS", new String[]{"FAVOURITE", "NAME"}, "NAME = ?", new String[]{mainActivity.buttonName}, null, null, null, null);
        // Start iteration!
        String isFavourite = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            isFavourite = cursor.getString(0);
        }

        assert isFavourite != null;
        if (isFavourite.equals("1")) {
            ToastUtils.showCustomToast(mainActivity, mainActivity.getResources().getString(R.string.removed_favourite));
            DBHandler.updateToolFavorite(mDatabase, mainActivity.buttonName, 0);
        } else {
            ToastUtils.showCustomToast(mainActivity, mainActivity.getResources().getString(R.string.added_favourite));
            DBHandler.updateToolFavorite(mDatabase, mainActivity.buttonName, 1);
        }

        restartSpinner();

        // Close cursor
        cursor.close();
    }

    // Changes app language
    public void changeLanguage(String languageCode) {
        Resources resources = mainActivity.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale newLocale = new Locale(languageCode);
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
