package com.cr4sh.nhlanucher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlanucher.bridge.Bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainUtils extends AppCompatActivity {

    public static String language;

    public static String languageLocale;
    public static String sortingMode;
    public static boolean isSetupCompleted;
    public static int fontSize;
    public static int buttonSize;
    public static boolean boolScription;

    public static String strokeColor;
    public static String fontName;

    // Colors
    public static String nameColor;
    public static String descriptionColor;
    public static String buttonColor;
    public static int buttonRadius;

    public static String frameColor;

    public static String logoIcon;
    public static List<String> valuesList;

    static List<Integer> imageList;

    private static SQLiteDatabase mDatabase;

    static MainActivity mainActivity;

    public static Typeface typeface;

    public static List<Item> itemList = new ArrayList<>();

    public MainUtils(MainActivity mainActivity) {
        MainUtils.mainActivity = mainActivity;
        mDatabase = mainActivity.mDatabase;
    }

    // MainUtils functions!!!
    public static final Object spinnerLock = new Object();

    public static void restartSpinner() {
        itemList.clear();
        // Reload spinner in the same position as it currently is
        Spinner spinner = mainActivity.findViewById(R.id.categoriesSpinner);
        int selectedItemText = spinner.getSelectedItemPosition();
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mainActivity, valuesList, imageList, buttonColor, nameColor);

        synchronized (spinnerLock) {
            new Thread(() -> mainActivity.runOnUiThread(() -> {
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(mainActivity);
                spinner.setSelection(selectedItemText);
            })).start();
        }
    }


    // NetHunter bridge function
    public static void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
        mainActivity.startActivity(intent);
    }

    // Increase button usage by 1
    public static void buttonUsageIncrease(String name) {
            DBHandler.updateToolUsage(mDatabase, name, mainActivity.buttonUsage + 1);
            restartSpinner();
    }

    // Removes all views (buttons) in our RecyclerView
    public static void deleteButtons() {
        RecyclerView layout = mainActivity.findViewById(R.id.recyclerView); // obtain a reference to the layout where the button will be added
        layout.removeAllViews();
    }

    // Queries db for buttons with given categories and display them!
    @SuppressLint({"SetTextI18n", "Recycle"})
    public static void spinnerChanger(String category) {
        // Obtain references to app resources and button layout
        Resources resources = mainActivity.getResources();
        RecyclerView layout = mainActivity.findViewById(R.id.recyclerView);
        TextView noToolsText = mainActivity.findViewById(R.id.messagebox);
        noToolsText.setText(null);

        Cursor cursor;

        String[] projection = {"CATEGORY", "FAVOURITE", "NAME", language, "CMD", "ICON", "USAGE"};
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

        cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, sortingMode, null);

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

            // Create the adapter and set it as the adapter for the RecyclerView
            if (layout.getAdapter() == null) {
                MyAdapter adapter = new MyAdapter(mainActivity, newItemList);
                layout.setAdapter(adapter);
                layout.setLayoutManager(new LinearLayoutManager(mainActivity));
            } else {
                // Update the itemList to reflect the latest list of items
                itemList.clear();
                itemList.addAll(newItemList);
                ((MyAdapter)layout.getAdapter()).updateData(itemList);
            }

        }
    }

    // Using deprecated methods for older android versions!!!!!
    public static void takePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", mainActivity.getPackageName(), null);
                intent.setData(uri);
                mainActivity.startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mainActivity.startActivityForResult(intent, 101);
            }
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    // Fills our spinner with text and images
    public static void createSpinner() {
        Spinner spinner = mainActivity.findViewById(R.id.categoriesSpinner);

        valuesList = Arrays.asList(mainActivity.getResources().getString(R.string.category_ft),
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
                mainActivity.getResources().getString(R.string.category_13));

        imageList = Arrays.asList(R.drawable.nhl_favourite_trans,
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

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mainActivity, valuesList, imageList, buttonColor, nameColor);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(mainActivity);

    }

    // Refreshes our TextView that is responsible for app background
    public static void refreshFrame() {
        @SuppressLint("DiscouragedApi") int frame = mainActivity.getResources().getIdentifier(frameColor, "drawable", mainActivity.getPackageName());
        @SuppressLint("DiscouragedApi") int logo = mainActivity.getResources().getIdentifier(logoIcon, "drawable", mainActivity.getPackageName());
        TextView changableTextView = mainActivity.findViewById(R.id.changableTextView);
        ImageView changableLogo = mainActivity.findViewById(R.id.nhlauncher_logo);
        changableTextView.setBackgroundResource(frame);
        changableLogo.setImageResource(logo);
        changableTextView.invalidate();
    }

    // Adds button to favourites bu updating FAVOURITE value
    public static void addFavourite() {

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

            MainUtils.restartSpinner();

            // Close cursor
            cursor.close();
    }

    // Get the color values from SharedPreferences
    public static void readColors(){
        buttonColor = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("buttonColor", "#4A4A4C");
        nameColor = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("nameColor", "#FFFFFF");
        descriptionColor = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("descriptionColor", "#e94b3c");
        buttonRadius = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getInt("buttonRadius", 10);
        strokeColor = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("strokeColor", "#4A4A4C");
        fontName = mainActivity.getSharedPreferences("customFonts", MODE_PRIVATE).getString("fontName", "roboto_bold");
        fontSize = mainActivity.getSharedPreferences("fontSizeSettings", MODE_PRIVATE).getInt("fontSize", 15);
        buttonSize = mainActivity.getSharedPreferences("buttonSettings", MODE_PRIVATE).getInt("buttonSize", 300);
        boolScription = mainActivity.getSharedPreferences("buttonSettings", MODE_PRIVATE).getBoolean("boolScription", true);
        frameColor = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("frameColor", "frame6");
        logoIcon = mainActivity.getSharedPreferences("customColors", MODE_PRIVATE).getString("logoIcon", "nhlauncher");
        typeface = Typeface.createFromAsset(mainActivity.getAssets(), "font/" + fontName + ".ttf");
    }

    // Get the settings values from SharedPreferences
    public static void readSettings(){
        language = mainActivity.getSharedPreferences("nhlSettings", MODE_PRIVATE).getString("language", "DESCRIPTION_" + Locale.getDefault().getLanguage());
        languageLocale = mainActivity.getSharedPreferences("nhlSettings", MODE_PRIVATE).getString("languageLocale", Locale.getDefault().getLanguage());
        sortingMode = mainActivity.getSharedPreferences("nhlSettings", MODE_PRIVATE).getString("sortingMode", null);
        isSetupCompleted =  mainActivity.getSharedPreferences("setupSettings", MODE_PRIVATE).getBoolean("isSetupCompleted", false);
    }

    // Changes app language
    public static void changeLanguage(String languageCode) {
        Resources resources = mainActivity.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale newLocale = new Locale(languageCode);
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}
