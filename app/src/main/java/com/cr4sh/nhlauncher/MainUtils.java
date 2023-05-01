package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.bridge.Bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainUtils extends AppCompatActivity {

    private final List<Integer> imageList;
    private final SQLiteDatabase mDatabase;
    private final MainActivity mainActivity;
    private final MyPreferences myPreferences;
    private List<String> valuesList;

//    private final List<Item> itemList;

    public MainUtils(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mDatabase = mainActivity.mDatabase;
//        itemList = new ArrayList<>();
        myPreferences = new MyPreferences(mainActivity);
        valuesList = Arrays.asList(
                mainActivity.getResources().getString(R.string.category_ft),
                mainActivity.getResources().getString(R.string.category_01),
                mainActivity.getResources().getString(R.string.category_02),
                mainActivity.getResources().getString(R.string.category_03),
                mainActivity.getResources().getString(R.string.category_04),
                mainActivity.getResources().getString(R.string.category_05),
                mainActivity.getResources().getString(R.string.category_06),
                mainActivity.getResources().getString(R.string.category_07),
                mainActivity.getResources().getString(R.string.category_08),
                mainActivity.getResources().getString(R.string.category_09),
                mainActivity.getResources().getString(R.string.category_10),
                mainActivity.getResources().getString(R.string.category_11),
                mainActivity.getResources().getString(R.string.category_12),
                mainActivity.getResources().getString(R.string.category_13)
        );
        imageList = Arrays.asList(
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
    }

    // MainUtils functions!!!

    // NetHunter bridge function
    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
        mainActivity.startActivity(intent);
    }

    // Increase button usage by 1
    public void buttonUsageIncrease(String name) {
        DBHandler.updateToolUsage(mDatabase, name, mainActivity.buttonUsage + 1);
        restartSpinner();
    }

    // Removes all views (buttons) in our RecyclerView
    public void deleteButtons() {
        RecyclerView layout = mainActivity.findViewById(R.id.recyclerView); // obtain a reference to the layout where the button will be added
        layout.removeAllViews();
    }

    // Queries db for buttons with given categories and display them!
    @SuppressLint({"SetTextI18n", "Recycle"})
    public void spinnerChanger(String category) {
        // Obtain references to app resources and button layout
        Resources resources = mainActivity.getResources();
        RecyclerView layout = mainActivity.findViewById(R.id.recyclerView);
        TextView noToolsText = mainActivity.findViewById(R.id.messagebox);
        noToolsText.setText(null);

        Cursor cursor;

        String[] projection = {"CATEGORY", "FAVOURITE", "NAME", myPreferences.language(), "CMD", "ICON", "USAGE"};
        String selection;
        String[] selectionArgs;

        if (category.contains("FT")) {
            selection = "FAVOURITE = ?";
            selectionArgs = new String[]{"1"};
            // Disable creating new buttons in fav category
            MainActivity.disableMenu = true;
        } else {
            String category_number = category.substring(0, 2);
            selection = "CATEGORY = ?";
            selectionArgs = new String[]{category_number};
            // Enable creating new buttons in normal categories
            MainActivity.disableMenu = false;
        }

        cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, myPreferences.sortingMode(), null);

        if (cursor.getCount() == 0) {
            noToolsText.setText(resources.getString(R.string.no_fav_tools));
            layout.setVisibility(View.GONE);
        } else {
            layout.scrollToPosition(0); // Scroll to first tool
            noToolsText.setText(null);
            layout.setVisibility(View.VISIBLE);
            // Create a new itemList from the cursor data
            List<Item> newItemList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String toolCategory = cursor.getString(0);
                String toolName = cursor.getString(2);
                String toolDescription = cursor.getString(3);
                String toolCmd = cursor.getString(4);
                String toolIcon = cursor.getString(5);
                int toolUsage = cursor.getInt(6);

                Item item = new Item(toolCategory, toolName, toolDescription, toolCmd, toolIcon, toolUsage);
                newItemList.add(item);

            }

            ((MyAdapter) Objects.requireNonNull(layout.getAdapter())).updateData(newItemList);


        }
        cursor.close();
    }

    // Fills our spinner with text and images
    public void restartSpinner() {

//        itemList.clear();

        Spinner spinner = mainActivity.findViewById(R.id.categoriesSpinner);

        int selectedItemText = spinner.getSelectedItemPosition();

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mainActivity, valuesList, imageList, myPreferences.buttonColor(), myPreferences.nameColor());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(mainActivity);
        spinner.setSelection(selectedItemText);

    }

    // Refreshes our TextView that is responsible for app background
    public void refreshFrame() {
        @SuppressLint("DiscouragedApi") int frame = mainActivity.getResources().getIdentifier(myPreferences.frameColor(), "drawable", mainActivity.getPackageName());
        @SuppressLint("DiscouragedApi") int logo = mainActivity.getResources().getIdentifier(myPreferences.logoIcon(), "drawable", mainActivity.getPackageName());
        TextView changeableTextView = mainActivity.findViewById(R.id.changeableTextView);
        ImageView changeableLogo = mainActivity.findViewById(R.id.nhlauncher_logo);
        changeableTextView.setBackgroundResource(frame);
        changeableLogo.setImageResource(logo);
        changeableTextView.invalidate();
    }

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
            Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.removed_favourite), Toast.LENGTH_SHORT).show();
            DBHandler.updateToolFavorite(mDatabase, mainActivity.buttonName, 0);
        } else {
            Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.added_favourite), Toast.LENGTH_SHORT).show();
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
        // Update valueList initialised before language changed!
        valuesList = Arrays.asList(
                mainActivity.getResources().getString(R.string.category_ft),
                mainActivity.getResources().getString(R.string.category_01),
                mainActivity.getResources().getString(R.string.category_02),
                mainActivity.getResources().getString(R.string.category_03),
                mainActivity.getResources().getString(R.string.category_04),
                mainActivity.getResources().getString(R.string.category_05),
                mainActivity.getResources().getString(R.string.category_06),
                mainActivity.getResources().getString(R.string.category_07),
                mainActivity.getResources().getString(R.string.category_08),
                mainActivity.getResources().getString(R.string.category_09),
                mainActivity.getResources().getString(R.string.category_10),
                mainActivity.getResources().getString(R.string.category_11),
                mainActivity.getResources().getString(R.string.category_12),
                mainActivity.getResources().getString(R.string.category_13)
        );
    }
}
