package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.cr4sh.nhlauncher.ButtonsRecycler.NHLAdapter;
import com.cr4sh.nhlauncher.ButtonsRecycler.NHLItem;
import com.cr4sh.nhlauncher.CategoriesRecycler.CategoriesAdapter;
import com.cr4sh.nhlauncher.Database.DBHandler;
import com.cr4sh.nhlauncher.SettingsPager.SettingsActivity;
import com.cr4sh.nhlauncher.SpecialFeatures.SpecialFeaturesActivity;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.MainUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.PermissionUtils;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    public static boolean disableMenu = false;
    public String buttonCategory;
    public String buttonName;
    public String buttonDescription;
    public String buttonCmd;
    public int buttonUsage;
    public SQLiteDatabase mDatabase;
    public ActivityResultLauncher<Intent> requestPermissionLauncher;
    public Button backButton;
    public int currentCategoryNumber = 1;
    private ImageView toolbar;
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();
    public RecyclerView recyclerView;
    private List<String> valuesList;
    private List<Integer> imageList;
    private TextView rollCategoriesText;
    private ImageView rollCategories;
    private MainUtils mainUtils;
    private com.cr4sh.nhlauncher.utils.NHLPreferences NHLPreferences;
    // Stop papysz easteregg
    private final Runnable stopPapysz = new Runnable() {
        @Override
        public void run() {
            mainUtils.restartSpinner();
        }
    };
    private EditText searchEditText;
    private LinearLayout rollCategoriesLayout;
    private RelativeLayout categoriesLayout;
    private ImageView searchIcon;
    private TextView noToolsText;
    private Animation rollOut;
    private Animation rollToolbar;
    private Animation rollOutToolbar;
    private GradientDrawable drawableSearchIcon;
    //     Set up a callback for the back button press
    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            closeOpen();
        }
    };

    private boolean permissionDialogShown = false;
    private boolean firstSetupDialogShown = false;

    @SuppressLint({"Recycle", "ResourceType", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.cat_appear, R.anim.cat_appear);
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        }
        NHLManager.getInstance().setMainActivity(this);
        DialogUtils dialogUtils = new DialogUtils(this.getSupportFragmentManager());

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

        PermissionUtils permissionUtils = new PermissionUtils(this);

        // Check for root permissions
        if (!permissionUtils.isRoot()) {
            dialogUtils.openRootDialog();
            return;
        }

        NHLPreferences = new NHLPreferences(this);

        resetRecyclerHeight();

        setContentView(R.layout.activity_main);

        View rootView = findViewById(android.R.id.content);

        // Apply custom colors
        rootView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(NHLPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(NHLPreferences.color20()));
//         Get the dialog and set it to not be cancelable
        setFinishOnTouchOutside(false);

        // Get classes
        DBHandler mDbHandler = DBHandler.getInstance(this);
        mDatabase = mDbHandler.getDatabase();

        // Check if setup has been completed
        if (!NHLPreferences.isSetupCompleted() && !firstSetupDialogShown) {
            dialogUtils.openFirstSetupDialog();
            firstSetupDialogShown = true;
        }

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });


        // Check for permissions
        if (!permissionUtils.isPermissionsGranted() && !permissionDialogShown) {
            dialogUtils.openPermissionsDialog();
            permissionDialogShown = true;
        }

        recyclerView = findViewById(R.id.recyclerView);

        // Set the adapter for RecyclerView
        NHLAdapter adapter = new NHLAdapter();
        recyclerView.setAdapter(adapter);

        // Get functions from this class
        mainUtils = new MainUtils(this);

        // Setup colors and settings
        mainUtils.changeLanguage(NHLPreferences.languageLocale());

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
        RecyclerView listViewCategories = findViewById(R.id.recyclerViewCategories);
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

        searchIcon = findViewById(R.id.searchIcon);
        searchEditText = findViewById(R.id.search_edit_text);
        toolbar = findViewById(R.id.toolBar);
        rollCategoriesLayout = findViewById(R.id.showCategoriesLayout);

        categoriesLayout = findViewById(R.id.categories_layout);
        TextView categoriesLayoutTitle = findViewById(R.id.dialog_title);
        Button specialButton = findViewById(R.id.special_features_button);
        backButton = findViewById(R.id.goBackButton);
        noToolsText = findViewById(R.id.messagebox);
        noToolsText.setTextColor(Color.parseColor(NHLPreferences.color80()));

        categoriesLayoutTitle.setTextColor(Color.parseColor(NHLPreferences.color80()));
        backButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        backButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        specialButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        specialButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        Animation categoriesAppear = AnimationUtils.loadAnimation(MainActivity.this, R.anim.cat_appear);

        rollCategoriesLayout.setOnClickListener(view -> runOnUiThread(() -> {
            VibrationUtils.vibrate(MainActivity.this, 10);

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
                categoriesLayout.startAnimation(categoriesAppear);

                enableAfterAnimation(categoriesLayout);
            }
        }));


        specialButton.setOnClickListener(v -> {
            enableAfterAnimation(toolbar);
            enableAfterAnimation(rollCategoriesLayout);
            disableWhileAnimation(categoriesLayout);

            Intent intent = new Intent(this, SpecialFeaturesActivity.class);
            Bundle animationBundle = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear  // Exit animation
            ).toBundle();
            startActivity(intent, animationBundle);
            backButton.callOnClick();
        });

        backButton.setOnClickListener(view -> {
            VibrationUtils.vibrate(MainActivity.this, 10);
            enableAfterAnimation(toolbar);
            enableAfterAnimation(rollCategoriesLayout);
            disableWhileAnimation(categoriesLayout);
            enableAfterAnimation(rollCategories);

            enableAfterAnimation(searchIcon);
            enableAfterAnimation(recyclerView);
            enableAfterAnimation(noToolsText);
            enableAfterAnimation(rollCategories);

        });

        searchIcon.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        searchEditText.setHintTextColor(Color.parseColor(NHLPreferences.color80()));
        searchEditText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        toolbar.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));

        @SuppressLint("UseCompatLoadingForDrawables") Drawable searchViewIcon = getDrawable(R.drawable.nhl_searchview);
        assert searchViewIcon != null;
        searchViewIcon.setTint(Color.parseColor(NHLPreferences.color80()));
        searchIcon.setImageDrawable(searchViewIcon);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable settingsIcon = getDrawable(R.drawable.nhl_settings);
        assert settingsIcon != null;
        settingsIcon.setTint(Color.parseColor(NHLPreferences.color80()));
        toolbar.setImageDrawable(settingsIcon);

        GradientDrawable drawableToolbar = new GradientDrawable();
        drawableToolbar.setCornerRadius(100);
        drawableToolbar.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        toolbar.setBackground(drawableToolbar);

        drawableSearchIcon = new GradientDrawable();
        drawableSearchIcon.setCornerRadius(100);
        drawableSearchIcon.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        searchIcon.setBackground(drawableSearchIcon);

        GradientDrawable drawableRollCategories = new GradientDrawable();
        drawableRollCategories.setCornerRadius(100);
        drawableRollCategories.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        rollCategoriesLayout.setBackground(drawableRollCategories);

        GradientDrawable drawableSearchEditText = new GradientDrawable();
        drawableSearchEditText.setCornerRadius(100);
        drawableSearchEditText.setColor(Color.parseColor(NHLPreferences.color50()));
        drawableSearchEditText.setStroke(8, Color.parseColor(NHLPreferences.color50()));

        // Setup animations
        Animation roll = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll);
        rollOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out);
        rollToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_toolbar);
        rollOutToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out_toolbar);

        searchIcon.setOnClickListener(v -> {
            VibrationUtils.vibrate(MainActivity.this, 10);

            if (searchEditText.getVisibility() == View.VISIBLE) {
                closeSearchBar();
            } else {
                // Disable things
                disableWhileAnimation(toolbar);
                disableWhileAnimation(rollCategoriesLayout);
                disableWhileAnimation(searchEditText);
                disableWhileAnimation(rollCategories);

                // Enable the searchEditText
                enableAfterAnimation(searchEditText);
                searchEditText.setVisibility(View.VISIBLE);

                // Set custom drawable as background
                searchEditText.setBackground(drawableSearchEditText);

                // Start animations simultaneously
                searchEditText.startAnimation(roll);
                rollCategoriesLayout.startAnimation(rollToolbar);
                toolbar.startAnimation(rollToolbar);

                roll.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Not needed for this case
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        searchEditText.requestFocus(); // Set focus when EditText is made visible
                        searchEditText.setSelection(searchEditText.getText().length()); // Move cursor to the end

                        // Show the keyboard explicitly
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // Not needed for this case
                    }
                });

                drawableSearchIcon.setSize(10, 10);
                drawableSearchIcon.setColor(Color.parseColor(NHLPreferences.color50()));
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {

//                Cursor cursor;

                String[] projection = {"CATEGORY", "NAME", NHLPreferences.language(), "CMD", "ICON", "USAGE"};

                // Add search filter to query
                String selection = "NAME LIKE ?";

                String[] selectionArgs = {"%" + newText + "%"};
//
                String orderBy = "CASE WHEN NAME LIKE '" + newText + "%' THEN 0 ELSE 1 END, " + // sort by first letter match
                        "CASE WHEN NAME LIKE '%" + newText + "%' THEN 0 ELSE 1 END, " + // sort by containing newText
                        "NAME ASC";

                if (newText.length() > 0) {
//                    recyclerView.setVisibility(View.VISIBLE);
                    disableMenu = true;

                    Future<List<NHLItem>> queryTask = executor.submit(() -> {
                        Cursor cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, orderBy, "15");

                        List<NHLItem> newItemList = new ArrayList<>();

                        if (cursor.getCount() > 0) {
                            // Create a new itemList from the cursor data
                            while (cursor.moveToNext()) {
                                String toolCategory = cursor.getString(0);
                                String toolName = cursor.getString(1);
                                String toolDescription = cursor.getString(2);
                                String toolCmd = cursor.getString(3);
                                String toolIcon = cursor.getString(4);
                                int toolUsage = cursor.getInt(5);

                                NHLItem item = new NHLItem(toolCategory, toolName, toolDescription, toolCmd, toolIcon, toolUsage);
                                newItemList.add(item);
                            }
                        } else {
                            runOnUiThread(() -> {
                                enableAfterAnimation(noToolsText);
                                noToolsText.setText(getResources().getString(R.string.cant_found) + newText + "\n" + getResources().getString(R.string.check_your_query));
                            });
                        }
                        cursor.close();
                        return newItemList;
                    });

                    runOnUiThread(() -> {
                        try {
                            List<NHLItem> newItemList1 = queryTask.get(); // This blocks until the task is done
                            if (newItemList1.isEmpty()) {
                                adapter.updateData(new ArrayList<>()); // Empty list to clear existing data

                                enableAfterAnimation(noToolsText);
                                noToolsText.setText(getResources().getString(R.string.db_error));
                            } else {
                                noToolsText.setText(null);
                                disableWhileAnimation(noToolsText);

                                adapter.updateData(newItemList1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

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


        toolbar.setOnClickListener(v -> executor.submit(()-> {
            VibrationUtils.vibrate(MainActivity.this, 10);
            Intent intent = new Intent(this, SettingsActivity.class);
            Bundle animationBundle = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear  // Exit animation
            ).toBundle();
            startActivity(intent, animationBundle);
        }));

        // Start papysz easteregg
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(currentTime);
        String targetTime = "21:37";

        // Compare the current time with the target time
        if (formattedTime.equals(targetTime)) {
            Handler handler = new Handler(Looper.getMainLooper());
            adapter.startPapysz();
            ToastUtils.showCustomToast(this, "21:37");
            handler.postDelayed(stopPapysz, 5000); // show papysz face for 5s
        }

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    public void disableWhileAnimation(View v) {
        runOnUiThread(() -> {
            v.setEnabled(false);
            v.setVisibility(View.GONE);
        });
    }

    public void enableAfterAnimation(View v) {
        runOnUiThread(() -> {
            v.setEnabled(true);
            v.setVisibility(View.VISIBLE);
        });
    }


    // Close database on app close
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
        }
        NHLManager.getInstance().shutdownExecutorService();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.cat_appear, R.anim.cat_appear);
            } else {
                overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
            }
        }
    }

    // Close searchbar or categories if back button pressed
    private void closeOpen() {
        if (searchEditText.getVisibility() == View.VISIBLE) {
            closeSearchBar();
        }
        if (categoriesLayout.getVisibility() == View.VISIBLE && searchEditText.getVisibility() == View.GONE) {
            backButton.callOnClick();
        }
    }

    private void closeSearchBar() {
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

        // Disable things
        disableWhileAnimation(searchEditText);
        drawableSearchIcon.setColor(Color.TRANSPARENT);

        // After animation
        rollCategoriesLayout.startAnimation(rollOutToolbar);
        toolbar.startAnimation(rollOutToolbar);
        searchEditText.startAnimation(rollOut);

        mainUtils.restartSpinner();
    }

    public void changeCategoryPreview(int position) {
        String categoryTextView;
        int imageResourceId;

        categoryTextView = valuesList.get(position);
        imageResourceId = imageList.get(position);

        // Set image resource and color filter
        rollCategories.setImageResource(imageResourceId);
        rollCategories.setColorFilter(Color.parseColor(NHLPreferences.color80()), PorterDuff.Mode.MULTIPLY);

        // Set text and text color
        rollCategoriesText.setText(categoryTextView);
        rollCategoriesText.setTextColor(Color.parseColor(NHLPreferences.color80()));
    }

    private void resetRecyclerHeight() {
        SharedPreferences prefs = getSharedPreferences("nhlSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("recyclerHeight", 0);
        editor.apply();
    }
}

