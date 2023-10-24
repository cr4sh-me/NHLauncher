package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static boolean disableMenu = false;
    public String buttonCategory;
    public String buttonName;
    public String buttonDescription;
    public String buttonCmd;
    public int buttonUsage;
    public SQLiteDatabase mDatabase;
    public ActivityResultLauncher<Intent> requestPermissionLauncher;
    private DialogUtils dialogUtils;
    private MainUtils mainUtils;
    private MyPreferences myPreferences;
    private RecyclerView recyclerView;

    @SuppressLint({"Recycle", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogUtils = new DialogUtils(this.getSupportFragmentManager());

        // Check for nethunter and terminal apps
//        PackageManager pm = getPackageManager();
//
//        try {
//            // First, check if the com.offsec.nethunter and com.offsec.nhterm packages exist
//            pm.getPackageInfo("com.offsec.nethunter", PackageManager.GET_ACTIVITIES);
//            pm.getPackageInfo("com.offsec.nhterm", PackageManager.GET_ACTIVITIES);
//
//            // Then, check if the com.offsec.nhterm.ui.term.NeoTermRemoteInterface activity exists within com.offsec.nhterm
//            Intent intent = new Intent();
//            intent.setComponent(new ComponentName("com.offsec.nhterm", "com.offsec.nhterm.ui.term.NeoTermRemoteInterface"));
//            List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//            if (activities.isEmpty()) {
//                // The activity is missing
//                dialogUtils.openMissingActivityDialog();
//                return;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            // One of the packages is missing
//            dialogUtils.openAppsDialog();
//            return;
//        }

        myPreferences = new MyPreferences(this);

        // Check for dynamic colors

//        if(myPreferences.dynamicThemeBool()){
//            Toast.makeText(this, "THEMED", Toast.LENGTH_SHORT).show();
//            setTheme(R.style.Themed_NHL);
//        }


        setContentView(R.layout.activity_main);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.roll);
        View rootView = findViewById(android.R.id.content);
        rootView.startAnimation(anim);

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

        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter for RecyclerView
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        // Get functions from this class
        mainUtils = new MainUtils(this);

        // Setup colors and settings
        mainUtils.changeLanguage(myPreferences.languageLocale());

        // Setting up spinner
        Spinner spinner = findViewById(R.id.categoriesSpinner);
        mainUtils.restartSpinner();

        // Check if there is any favourite tool in db, and open Favourite Tools category by default
        int isFavourite = 0;
        String selection = "FAVOURITE = ?";
        String[] selectionArgs = {"1"};
        try (Cursor cursor = mDatabase.query("TOOLS", new String[]{"COUNT(FAVOURITE)"}, selection, selectionArgs, "FAVOURITE = 1", "FAVOURITE = 1", "1", "1")) {
            if (cursor.moveToFirst()) {
                isFavourite = cursor.getInt(0);
            }
        }

        // Set category, so code below can run
        spinner.setSelection(isFavourite == 0 ? 1 : 0);
        // Add onclick listener for toolbar
//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView searchIcon = findViewById(R.id.searchIcon);
        EditText searchEditText = findViewById(R.id.search_edit_text);
        ImageView toolbar = findViewById(R.id.toolBar);

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

        GradientDrawable drawableSearchEditText = new GradientDrawable();
        drawableSearchEditText.setCornerRadius(100);
        drawableSearchEditText.setColor(Color.parseColor(myPreferences.color50()));
        drawableSearchEditText.setStroke(8, Color.parseColor(myPreferences.color50()));


        // Setup animations
        Animation roll = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll);
        Animation rollOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out);
        Animation rollToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_toolbar);
        Animation rollOutToolbar = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_out_toolbar);
        Animation spinnerFadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spinner_fade);
        Animation spinnerFadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spinner_fade_out);
        Animation recyclerPullUp = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rec_up);
        Animation recyclerPullDown = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rec_down);



        searchIcon.setOnClickListener(v -> {
            // Toggle visibility of the search EditText when the icon is clicked
            if (searchEditText.getVisibility() == View.VISIBLE) {

                // Close the keyboard if it's open
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                // Show toolbar
                toolbar.startAnimation(rollOutToolbar);
                toolbar.setVisibility(View.VISIBLE);

                // Show spinner
                mainUtils.restartSpinner();
                spinner.startAnimation(recyclerPullDown);
                spinner.setVisibility(View.VISIBLE);


                // Animate recyclerView
                recyclerView.startAnimation(recyclerPullDown);

                // Animate searchbar
                searchEditText.startAnimation(rollOut);
                searchEditText.setVisibility(View.GONE);
                searchEditText.setText(null);

                // Change searchIcon background
                drawableSearchIcon.setColor(Color.TRANSPARENT);

                // Enable settings
                toolbar.setEnabled(true);

                // Enable spinner
                spinner.setEnabled(true);

            } else {

                // Disable settings
                toolbar.setEnabled(false);

                // Disable spinner
                spinner.setEnabled(false);

                // Hide settings
                toolbar.startAnimation(rollToolbar);
                toolbar.setVisibility(View.GONE);

                // Hide spinner
                spinner.startAnimation(recyclerPullUp);
                spinner.setVisibility(View.GONE);

                // Animate recyclerView
                recyclerView.startAnimation(recyclerPullUp);

                // Show searchbar
                searchEditText.startAnimation(roll);
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus(); // Set focus when EditText is made visible

                // Add searchbar background
                searchEditText.setBackground(drawableSearchEditText);

                // Change searchIcon background
                drawableSearchIcon.setSize(10, 10);
                drawableSearchIcon.setColor(Color.parseColor(myPreferences.color50()));

//                    searchIcon.setBackground(drawableSearchEditText);

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
                mainUtils.deleteButtons();

                TextView noToolsText = findViewById(R.id.messagebox);
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

                } else {
                    noToolsText.startAnimation(myAnimation);
                    runOnUiThread(() -> {
                        disableMenu = false;
                        noToolsText.setText(getResources().getString(R.string.no_newtext_entry));
                        // Clear the list of items
                        recyclerView.setVisibility(View.GONE);

                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toolbar.setOnClickListener(v -> dialogUtils.openToolbarDialog(MainActivity.this));

    }

    // Close database on app close
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    // run spinnerChanger with selected position as parameter
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        mainUtils.spinnerChanger(text);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            case R.id.option_1:
                dialogUtils.openEditableDialog(buttonName, buttonCmd);
                return true;
            case R.id.option_2:
                mainUtils.addFavourite();
                return true;
            case R.id.option_3:
                if (!disableMenu) {
                    dialogUtils.openNewToolDialog(buttonCategory);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.get_out), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.option_4:
                dialogUtils.openDeleteToolDialog(buttonName);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}

