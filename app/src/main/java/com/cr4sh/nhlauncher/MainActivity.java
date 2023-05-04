package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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

    @SuppressLint("Recycle")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogUtils = new DialogUtils(this.getSupportFragmentManager());

        // Check for nethunter and terminal apps
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.offsec.nethunter", PackageManager.GET_ACTIVITIES);
            pm.getPackageInfo("com.offsec.nhterm", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            dialogUtils.openAppsDialog();
            return;
        }

        setContentView(R.layout.activity_main);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.roll);
        View rootView = findViewById(android.R.id.content);
        rootView.startAnimation(anim);

        // Get the dialog and set it to not be cancelable
        setFinishOnTouchOutside(false);

        // Get classes
        DBHandler mDbHandler = DBHandler.getInstance(this);
        mDatabase = mDbHandler.getDatabase();
        myPreferences = new MyPreferences(this);
        PermissionUtils permissionUtils = new PermissionUtils(this);


//         Check if setup has been completed
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

        // Set the adapter with empty itemList
//        List<Item> itemList = new ArrayList<>();
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
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        mainUtils.refreshFrame();

        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {
//                itemList.clear();

                // Limit text input to 25 characters
                InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(25)};
                EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
                searchEditText.setFilters(filters);

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

//                            menuItem.setEnabled(false);
                    disableMenu = true;

                    cursor = mDatabase.query("TOOLS", projection, selection, selectionArgs, null, null, orderBy, "15");

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
                    return true;
                } else {
                    noToolsText.startAnimation(myAnimation);
                    runOnUiThread(() -> {
                        disableMenu = false;
                        noToolsText.setText(getResources().getString(R.string.no_newtext_entry));
                        // Clear the list of items
                        recyclerView.setVisibility(View.GONE);

                    });
                    return false;
                }
            }
        });

        searchView.setOnCloseListener(() -> {
            if (!searchView.getQuery().toString().isEmpty()) {
                return false;
            }
            spinner.setEnabled(true);
            toolbar.setEnabled(true);
            spinner.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            mainUtils.restartSpinner();
            return false;
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            // If the search view has focus, disable and hide the spinner
            if (hasFocus) {
                spinner.setEnabled(false);
                toolbar.setEnabled(false);
                spinner.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
            }
        });

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
        s.setSpan(new ForegroundColorSpan(Color.parseColor(myPreferences.nameColor())), 0, s.length(), 0);
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


    // Creates toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Catches options for toolbar menu above
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_1:
                dialogUtils.openSettingsDialog();
                return true;
            case R.id.menu_item_2:
                closeOptionsMenu();
                dialogUtils.openCustomThemesDialog();
                return true;
            case R.id.menu_item_3:
                dialogUtils.openStatisticsDialog();
                return true;
            case R.id.menu_item_4:
                // Just open link in browser
                String url = "https://github.com/cr4sh-me/NHLauncher";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

