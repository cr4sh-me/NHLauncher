package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.ButtonsRecycler.Item;
import com.cr4sh.nhlauncher.ButtonsRecycler.MyAdapter;
import com.cr4sh.nhlauncher.CategoriesRecycler.CategoriesAdapter;
import com.cr4sh.nhlauncher.Database.DBHandler;
import com.cr4sh.nhlauncher.SettingsPager.SettingsActivity;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.PermissionUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //    public boolean isSpecialEnabled = false;
    public static boolean disableMenu = false;
    public String buttonCategory;
    public String buttonName;
    public String buttonDescription;
    public String buttonCmd;
    public int buttonUsage;
    public SQLiteDatabase mDatabase;
    public ActivityResultLauncher<Intent> requestPermissionLauncher;
    public RecyclerView listViewCategories;
    public Button backButton;
    public Button specialButton;
    public int currentCategoryNumber = 1;
    public ImageView toolbar;
    List<String> valuesList;
    List<Integer> imageList;
    TextView rollCategoriesText;
    ImageView rollCategories;
    private DialogUtils dialogUtils;
    private MainUtils mainUtils;
    private MyPreferences myPreferences;
    private RecyclerView recyclerView;
    public ExecutorService executor;

    // TODO replace new Thread() with ExecutorService
    // TODO replace oroginal toasts with custom one
    // TODO translation
    // TODO test app stability
    @SuppressLint({"Recycle", "ResourceType", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NHLManager.getInstance().setMainActivity(this);
        dialogUtils = new DialogUtils(this.getSupportFragmentManager());
        executor = Executors.newCachedThreadPool();

        // Check for nethunter and terminal apps
        PackageManager pm = getPackageManager();

        try {
            // First, check if the com.offsec.nethunter and com.offsec.nhterm packages exist
            pm.getPackageInfo("com.offsec.nethunter", PackageManager.GET_ACTIVITIES);
            pm.getPackageInfo("com.offsec.nhterm", PackageManager.GET_ACTIVITIES);

            // Then, check if the com.offsec.nhterm.ui.term.NeoTermRemoteInterface activity exists within com.offsec.nhterm
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.offsec.nhterm", "com.offsec.nhterm.ui.term.NeoTermRemoteInterface"));
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if (activities.isEmpty()) {
                // The activity is missing
                dialogUtils.openMissingActivityDialog();
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // One of the packages is missing
            dialogUtils.openAppsDialog();
            return;
        }

        myPreferences = new MyPreferences(this);

        setContentView(R.layout.activity_main);

        View rootView = findViewById(android.R.id.content);

        // Apply custom colors
        rootView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(myPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(myPreferences.color20()));
//         Get the dialog and set it to not be cancelable
        setFinishOnTouchOutside(false);

        // Get classes
        DBHandler mDbHandler = DBHandler.getInstance(this);
        mDatabase = mDbHandler.getDatabase();
        PermissionUtils permissionUtils = new PermissionUtils(this);

        // Check if setup has been completed
        if (!myPreferences.isSetupCompleted()) {
            dialogUtils.openFirstSetupDialog();
        }

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });


        // Check for permissions
        if (!permissionUtils.isPermissionsGranted()) {
            dialogUtils.openPermissionsDialog();
        }

        recyclerView = findViewById(R.id.recyclerView);

        // Set the adapter for RecyclerView
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        // Get functions from this class
        mainUtils = new MainUtils(this);

        // Setup colors and settings
        mainUtils.changeLanguage(myPreferences.languageLocale());

        // Setting up new spinner

        valuesList = Arrays.asList(
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
                getResources().getString(R.string.category_12),
                getResources().getString(R.string.category_13)
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

//        adapter2 = new CustomSpinnerAdapter(this, valuesList, imageList, myPreferences.color20(), myPreferences.color80());
        listViewCategories = findViewById(R.id.recyclerViewCategories);
        CategoriesAdapter adapter2 = new CategoriesAdapter();

        // Fill categories recycler
        adapter2.updateData(valuesList, imageList);
        listViewCategories.setAdapter(adapter2);

        // Initialise categories



//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        listViewCategories.setLayoutManager(layoutManager);


//        Spinner spinner = findViewById(R.id.categoriesSpinner);
//        mainUtils.restartSpinner();

        // Check if there is any favourite tool in db, and open Favourite Tools category by default
        int isFavourite = 0;
        String selection = "FAVOURITE = ?";
        String[] selectionArgs = {"1"};
        try (Cursor cursor = mDatabase.query("TOOLS", new String[]{"COUNT(FAVOURITE)"}, selection, selectionArgs, "FAVOURITE = 1", "FAVOURITE = 1", "1", "1")) {
            if (cursor.moveToFirst()) {
                isFavourite = cursor.getInt(0);
            }
        }

        rollCategories = findViewById(R.id.showCategoriesImage); // Init rollCategories before spinnerChanger method
        rollCategoriesText = findViewById(R.id.showCategoriesText); // Init rollCategoruesText before spinnerChanger method

        mainUtils.spinnerChanger(isFavourite == 0 ? 1 : 0);
        currentCategoryNumber = isFavourite == 0 ? 1 : 0;

        ImageView searchIcon = findViewById(R.id.searchIcon);
        EditText searchEditText = findViewById(R.id.search_edit_text);
        toolbar = findViewById(R.id.toolBar);
        LinearLayout rollCategoriesLayout = findViewById(R.id.showCategoriesLayout);

        RelativeLayout categoriesLayout = findViewById(R.id.categories_layout);
        TextView categoriesLayoutTitle = findViewById(R.id.dialog_title);
        specialButton = findViewById(R.id.special_features_button);
        backButton = findViewById(R.id.goBackButton);
        TextView noToolsText = findViewById(R.id.messagebox);

        categoriesLayoutTitle.setTextColor(Color.parseColor(myPreferences.color80()));
        backButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        backButton.setTextColor(Color.parseColor(myPreferences.color50()));

        specialButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        specialButton.setTextColor(Color.parseColor(myPreferences.color80()));


        Animation recUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rec_down);

        rollCategoriesLayout.setOnClickListener(view -> runOnUiThread(() -> {

            // Check if searchView is not opened, prevent opening 2 things at the same time
            if (searchEditText.getVisibility() == View.GONE) {

                // Disable things
                disableWhileAnimation(categoriesLayout);
                disableWhileAnimation(searchIcon);
                disableWhileAnimation(noToolsText);
                disableWhileAnimation(searchIcon);
                disableWhileAnimation(recyclerView);
                disableWhileAnimation(toolbar);
                disableWhileAnimation(rollCategories);
                disableWhileAnimation(rollCategoriesLayout);

                // Animation

                VibrationUtil.vibrate(MainActivity.this, 10);
                categoriesLayout.startAnimation(recUp);

                // Enable things

                enableAfterAnimation(categoriesLayout);
                enableAfterAnimation(listViewCategories);
            }
        }));

        specialButton.setOnClickListener(v -> {
            enableAfterAnimation(toolbar);
            enableAfterAnimation(rollCategoriesLayout);
            disableWhileAnimation(categoriesLayout);


            backButton.callOnClick();

            Intent intent = new Intent(this, SpecialFeaturesActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {

            enableAfterAnimation(toolbar);
            enableAfterAnimation(rollCategoriesLayout);
            disableWhileAnimation(categoriesLayout);
            enableAfterAnimation(rollCategories);


            enableAfterAnimation(searchIcon);
            enableAfterAnimation(recyclerView);
            enableAfterAnimation(noToolsText);
            enableAfterAnimation(rollCategories);

        });

        searchIcon.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        searchEditText.setHintTextColor(Color.parseColor(myPreferences.color80()));
        searchEditText.setTextColor(Color.parseColor(myPreferences.color80()));
        toolbar.setBackgroundColor(Color.parseColor(myPreferences.color50()));

        @SuppressLint("UseCompatLoadingForDrawables") Drawable searchViewIcon = getDrawable(R.drawable.nhl_searchview);
        assert searchViewIcon != null;
        searchViewIcon.setTint(Color.parseColor(myPreferences.color80()));
        searchIcon.setImageDrawable(searchViewIcon);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable settingsIcon = getDrawable(R.drawable.nhl_settings);
        assert settingsIcon != null;
        settingsIcon.setTint(Color.parseColor(myPreferences.color80()));
        toolbar.setImageDrawable(settingsIcon);

        GradientDrawable drawableToolbar = new GradientDrawable();
        drawableToolbar.setCornerRadius(100);
        drawableToolbar.setStroke(8, Color.parseColor(myPreferences.color50()));
        toolbar.setBackground(drawableToolbar);

        GradientDrawable drawableSearchIcon = new GradientDrawable();
        drawableSearchIcon.setCornerRadius(100);
        drawableSearchIcon.setStroke(8, Color.parseColor(myPreferences.color50()));
        searchIcon.setBackground(drawableSearchIcon);

        GradientDrawable drawableRollCategories = new GradientDrawable();
        drawableRollCategories.setCornerRadius(100);
        drawableRollCategories.setStroke(8, Color.parseColor(myPreferences.color50()));
        rollCategoriesLayout.setBackground(drawableRollCategories);

        GradientDrawable drawableSearchEditText = new GradientDrawable();
        drawableSearchEditText.setCornerRadius(100);
        drawableSearchEditText.setColor(Color.parseColor(myPreferences.color50()));
        drawableSearchEditText.setStroke(8, Color.parseColor(myPreferences.color50()));


        // Setup animations
        Animation roll = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll);
        Animation rollOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out);
        Animation rollToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_toolbar);
        Animation rollOutToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out_toolbar);

        searchIcon.setOnClickListener(v -> {
            VibrationUtil.vibrate(MainActivity.this, 10);

            // Toggle visibility of the search EditText when the icon is clicked
            if (searchEditText.getVisibility() == View.VISIBLE) {

                // Clear searchbar, every close
                searchEditText.setText(null);

                // Enable things
                enableAfterAnimation(toolbar);
                enableAfterAnimation(rollCategoriesLayout);
                enableAfterAnimation(rollCategories);
                enableAfterAnimation(searchEditText);

                // Close the keyboard if it's open
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                // After animation
                rollCategoriesLayout.startAnimation(rollOutToolbar);
                toolbar.startAnimation(rollOutToolbar);
                searchEditText.startAnimation(rollOut);

                // Disable things
                disableWhileAnimation(searchEditText);
                drawableSearchIcon.setColor(Color.TRANSPARENT);

                mainUtils.restartSpinner();
            } else {

                // Disable things
                disableWhileAnimation(toolbar);
                disableWhileAnimation(rollCategoriesLayout);
                disableWhileAnimation(searchEditText);
                disableWhileAnimation(rollCategories);

                toolbar.startAnimation(rollToolbar);
                rollCategoriesLayout.startAnimation(rollToolbar);
                searchEditText.startAnimation(roll);

                enableAfterAnimation(searchEditText);
                searchEditText.requestFocus(); // Set focus when EditText is made visible
                searchEditText.setBackground(drawableSearchEditText);

                drawableSearchIcon.setSize(10, 10);
                drawableSearchIcon.setColor(Color.parseColor(myPreferences.color50()));

                // Show the keyboard explicitly
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {

                Cursor cursor;

                String[] projection = {"CATEGORY", "NAME", myPreferences.language(), "CMD", "ICON", "USAGE"};

                // Add search filter to query
                String selection = "NAME LIKE ?";

                String[] selectionArgs = {"%" + newText + "%"};

                String orderBy = "CASE WHEN NAME LIKE '" + newText + "%' THEN 0 ELSE 1 END, " + // sort by first letter match
                        "CASE WHEN NAME LIKE '%" + newText + "%' THEN 0 ELSE 1 END, " + // sort by containing newText
                        "NAME ASC";

                Animation myAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                if (newText.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    disableMenu = true;

                    cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, orderBy, "15");

                    noToolsText.setTextColor(Color.parseColor(myPreferences.color80()));
                    noToolsText.startAnimation(myAnimation);
                    // Run OnUiThread to edit layout!
                    if (cursor.getCount() == 0) {
                        runOnUiThread(() -> noToolsText.setText(getResources().getString(R.string.cant_found) + newText + "\n" + getResources().getString(R.string.check_your_query)));
                    }
                    List<Item> newItemList = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        noToolsText.setText(null);
                        String toolCategory = cursor.getString(0);
                        String toolName = cursor.getString(1);
                        String toolDescription = cursor.getString(2);
                        String toolCmd = cursor.getString(3);
                        String toolIcon = cursor.getString(4);
                        int toolUsage = cursor.getInt(5);

                        Log.d("TESTER", cursor.getString(1));

                        Item item = new Item(toolCategory, toolName, toolDescription, toolCmd, toolIcon, toolUsage);

                        // Add the item to the itemList
                        newItemList.add(item);

                    }
                    adapter.updateData(newItemList);
                    cursor.close();

                    // Prevent newlines from being entered
                    if (newText.toString().contains("\n")) {
                        String filteredText = newText.toString().replace("\n", "");
                        searchEditText.setText(filteredText);
                        searchEditText.setSelection(filteredText.length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toolbar.setOnClickListener(v -> {
//            toolbar.setEnabled(false); // Prevent double open
            VibrationUtil.vibrate(MainActivity.this, 10);
//            dialogUtils.openToolbarDialog(MainActivity.this);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });


    }

    private void disableWhileAnimation(View v) {
        v.setEnabled(false);
        v.setVisibility(View.GONE);
    }

    private void enableAfterAnimation(View v) {
        v.setEnabled(true);
        v.setVisibility(View.VISIBLE);
    }

    // Close database on app close
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
        }
        if(!executor.isShutdown()){
            executor.shutdown();
        }
    }



    // Creates menu that is shown after longer button click
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Set the color of the menu title
        SpannableString s = new SpannableString(getResources().getString(R.string.choose_option));
        Objects.requireNonNull(menu.setHeaderTitle(s));
        getMenuInflater().inflate(R.menu.options_menu, menu);
    }

    // Catches selections for menu above
    @SuppressLint("NonConstantResourceId")
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_1 -> {
                dialogUtils.openEditableDialog(buttonName, buttonCmd);
                return true;
            }
            case R.id.option_2 -> {
                mainUtils.addFavourite();
                return true;
            }
            case R.id.option_3 -> {
                if (!disableMenu) {
                    dialogUtils.openNewToolDialog(buttonCategory);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.get_out), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.option_4 -> {
                dialogUtils.openDeleteToolDialog(buttonName);
                return true;
            }
            default -> {
                return super.onContextItemSelected(item);
            }
        }
    }


    public void changeCategoryPreview(int position) {
        String categoryTextView;
        int imageResourceId;

        categoryTextView = valuesList.get(position);
        imageResourceId = imageList.get(position);

        // Set image resource and color filter
        rollCategories.setImageResource(imageResourceId);
        rollCategories.setColorFilter(Color.parseColor(myPreferences.color80()), PorterDuff.Mode.MULTIPLY);

        // Set text and text color
        rollCategoriesText.setText(categoryTextView);
        rollCategoriesText.setTextColor(Color.parseColor(myPreferences.color80()));
    }
}

