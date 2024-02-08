package com.cr4sh.nhlauncher

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.buttonsRecycler.NHLAdapter
import com.cr4sh.nhlauncher.buttonsRecycler.NHLItem
import com.cr4sh.nhlauncher.categoriesRecycler.CategoriesAdapter
import com.cr4sh.nhlauncher.database.DBHandler
import com.cr4sh.nhlauncher.settingsPager.SettingsActivity
import com.cr4sh.nhlauncher.specialFeatures.SpecialFeaturesActivity
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.LanguageChanger
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.NHLUtils
import com.cr4sh.nhlauncher.utils.PermissionUtils
import com.cr4sh.nhlauncher.utils.ToastUtils
import com.cr4sh.nhlauncher.utils.VibrationUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //    private val executor = NHLManager.getInstance().executorService
    var buttonCategory: String? = null
    var buttonName: String? = null
    var buttonDescription: String? = null
    var buttonCmd: String? = null
    var buttonUsage = 0
    lateinit var mDatabase: SQLiteDatabase
    lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>
    lateinit var backButton: Button
    var currentCategoryNumber = 1
    lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: ImageView
    private lateinit var valuesList: List<String>
    private lateinit var imageList: List<Int>
    private lateinit var rollCategoriesText: TextView
    private lateinit var rollCategories: ImageView
    lateinit var mainUtils: NHLUtils
    lateinit var nhlPreferences: NHLPreferences

    // Stop papysz easteregg
    private var stopPapysz = Runnable { mainUtils.restartSpinner() }
    lateinit var searchEditText: EditText
    private lateinit var rollCategoriesLayout: LinearLayout
    private lateinit var categoriesLayout: RelativeLayout
    private lateinit var searchIcon: ImageView
    lateinit var noToolsText: TextView
    private lateinit var rollOut: Animation
    private lateinit var rollToolbar: Animation
    private lateinit var rollOutToolbar: Animation
    private lateinit var drawableSearchIcon: GradientDrawable

    //     Set up a callback for the back button press
    private var onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeOpen()
            }
        }
    private var permissionDialogShown = false
    private var firstSetupDialogShown = false

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("Recycle", "ResourceType", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NHLManager.instance.mainActivity = this


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.cat_appear,
                R.anim.cat_appear
            )
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
        }
        val dialogUtils = DialogUtils(this.supportFragmentManager)

        // Check for nethunter and terminal apps
//        val pm = packageManager
//        try {
//            // First, check if the com.offsec.nethunter and com.offsec.nhterm packages exist
//            pm.getPackageInfo("com.offsec.nethunter", PackageManager.GET_ACTIVITIES)
//            pm.getPackageInfo("com.offsec.nhterm", PackageManager.GET_ACTIVITIES)
//
//            // Then, check if the com.offsec.nhterm.ui.term.NeoTermRemoteInterface activity exists within com.offsec.nhterm
//            val intent = Intent()
//            intent.setComponent(
//                ComponentName(
//                    "com.offsec.nhterm",
//                    "com.offsec.nhterm.ui.term.NeoTermRemoteInterface"
//                )
//            )
//            val activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//            if (activities.isEmpty()) {
//                // The activity is missing
//                dialogUtils.openMissingActivityDialog()
//                return
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            // One of the packages is missing
//            dialogUtils.openAppsDialog()
//            return
//        }
        val permissionUtils = PermissionUtils(this)

        // Check for root permissions
//        if (!permissionUtils.isRoot) {
//            dialogUtils.openRootDialog()
//            return
//        }
        nhlPreferences = NHLPreferences(this)

        val languageChanger = LanguageChanger()
        nhlPreferences.languageLocale()?.let { languageChanger.setLocale(this@MainActivity, it) }


        // set language

        resetRecyclerHeight()
        setContentView(R.layout.activity_main)


        val rootView = findViewById<View>(android.R.id.content)

        // Apply custom colors
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences.color20())
        //         Get the dialog and set it to not be cancelable
        setFinishOnTouchOutside(false)

        // Get classes
        val mDbHandler = DBHandler.getInstance(this)
        if (mDbHandler != null) {
            mDatabase = mDbHandler.database
        }

        // Check if setup has been completed
        if (!nhlPreferences.isSetupCompleted && !firstSetupDialogShown) {
            dialogUtils.openFirstSetupDialog()
            firstSetupDialogShown = true
        }
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

        // Check for permissions
        if (!permissionUtils.isPermissionsGranted && !permissionDialogShown) {
            dialogUtils.openPermissionsDialog()
            permissionDialogShown = true
        }
        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.search_edit_text)

        // Set the adapter for RecyclerView
        val adapter = NHLAdapter(searchEditText)
        recyclerView.adapter = adapter

        // Get functions from this class
        mainUtils = NHLUtils(this)
        // Setup colors and settings
// Assuming mainUtils.changeLanguage is a suspend function


        // Setting up new spinner
        valuesList = listOf(
            resources.getString(R.string.category_ft),
            resources.getString(R.string.category_01),
            resources.getString(R.string.category_02),
            resources.getString(R.string.category_03),
            resources.getString(R.string.category_04),
            resources.getString(R.string.category_05),
            resources.getString(R.string.category_06),
            resources.getString(R.string.category_07),
            resources.getString(R.string.category_08),
            resources.getString(R.string.category_09),
            resources.getString(R.string.category_10),
            resources.getString(R.string.category_11),
            resources.getString(R.string.category_12),
            resources.getString(R.string.category_13)
        )
        imageList = listOf(
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
        )
        val listViewCategories = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        val adapter2 = CategoriesAdapter()

        // Fill categories recycler
        // Assuming mainUtils.changeLanguage is a suspend function
        GlobalScope.launch(Dispatchers.Default) {
            adapter2.updateData(valuesList, imageList)
            listViewCategories.adapter = adapter2
        }

        // Check if there is any favourite tool in db, and open Favourite Tools category by default
        // Check if there is any favourite tool in db, and open Favourite Tools category by default
        val isFavourite: Int = try {
            runBlocking {
                withContext(Dispatchers.Default) {
                    var isFavourite = 0
                    try {
                        val selection = "FAVOURITE = ?"
                        val selectionArgs = arrayOf("1")

                        mDatabase.query(
                            "TOOLS",
                            arrayOf("COUNT(FAVOURITE)"),
                            selection,
                            selectionArgs,
                            "FAVOURITE = 1",
                            "FAVOURITE = 1",
                            "1",
                            "1"
                        ).use { cursor ->
                            if (cursor.moveToFirst()) {
                                isFavourite = cursor.getInt(0)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    isFavourite
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0 // Default value in case of an exception
        }

        rollCategories =
            findViewById(R.id.showCategoriesImage) // Init rollCategories before spinnerChanger method
        rollCategoriesText =
            findViewById(R.id.showCategoriesText) // Init rollCategoriesText before spinnerChanger method
        mainUtils.spinnerChanger(if (isFavourite == 0) 1 else 0)
        currentCategoryNumber = if (isFavourite == 0) 1 else 0
        searchIcon = findViewById(R.id.searchIcon)
        toolbar = findViewById(R.id.toolBar)
        rollCategoriesLayout = findViewById(R.id.showCategoriesLayout)
        categoriesLayout = findViewById(R.id.categories_layout)
        val categoriesLayoutTitle = findViewById<TextView>(R.id.dialog_title)
        val specialButton = findViewById<Button>(R.id.special_features_button)
        backButton = findViewById(R.id.goBackButton)
        noToolsText = findViewById(R.id.messagebox)
        noToolsText.setTextColor(Color.parseColor(nhlPreferences.color80()))
        categoriesLayoutTitle.setTextColor(Color.parseColor(nhlPreferences.color80()))
        backButton.setBackgroundColor(Color.parseColor(nhlPreferences.color80()))
        backButton.setTextColor(Color.parseColor(nhlPreferences.color50()))
        specialButton.setBackgroundColor(Color.parseColor(nhlPreferences.color50()))
        specialButton.setTextColor(Color.parseColor(nhlPreferences.color80()))
        val categoriesAppear = AnimationUtils.loadAnimation(this@MainActivity, R.anim.cat_appear)
        rollCategoriesLayout.setOnClickListener {
            runOnUiThread {
                VibrationUtils.vibrate(this@MainActivity, 10)

                // Check if searchView is not opened, prevent opening 2 things at the same time
                if (searchEditText.visibility == View.GONE) {

                    // Disable things
                    disableWhileAnimation(categoriesLayout)
                    disableWhileAnimation(searchIcon)
                    disableWhileAnimation(noToolsText)
                    disableWhileAnimation(searchIcon)
                    disableWhileAnimation(recyclerView)
                    disableWhileAnimation(toolbar)
                    disableWhileAnimation(rollCategories)
                    disableWhileAnimation(rollCategoriesLayout)

                    // Animation
                    categoriesLayout.startAnimation(categoriesAppear)
                    enableAfterAnimation(categoriesLayout)
                }
            }
        }
        specialButton.setOnClickListener {
            enableAfterAnimation(toolbar)
            enableAfterAnimation(rollCategoriesLayout)
            disableWhileAnimation(categoriesLayout)
            val intent = Intent(this, SpecialFeaturesActivity::class.java)
            val animationBundle = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.cat_appear,  // Enter animation
                R.anim.cat_disappear // Exit animation
            ).toBundle()
            startActivity(intent, animationBundle)
            backButton.callOnClick()
        }
        backButton.setOnClickListener {
            VibrationUtils.vibrate(this@MainActivity, 10)
            enableAfterAnimation(toolbar)
            enableAfterAnimation(rollCategoriesLayout)
            disableWhileAnimation(categoriesLayout)
            enableAfterAnimation(rollCategories)
            enableAfterAnimation(searchIcon)
            enableAfterAnimation(recyclerView)
            enableAfterAnimation(noToolsText)
            enableAfterAnimation(rollCategories)
        }
        searchIcon.setBackgroundColor(Color.parseColor(nhlPreferences.color50()))
        searchEditText.setHintTextColor(Color.parseColor(nhlPreferences.color80()))
        searchEditText.setTextColor(Color.parseColor(nhlPreferences.color80()))
        toolbar.setBackgroundColor(Color.parseColor(nhlPreferences.color50()))
        @SuppressLint("UseCompatLoadingForDrawables") val searchViewIcon =
            getDrawable(R.drawable.nhl_searchview)!!
        searchViewIcon.setTint(Color.parseColor(nhlPreferences.color80()))
        searchIcon.setImageDrawable(searchViewIcon)
        @SuppressLint("UseCompatLoadingForDrawables") val settingsIcon =
            getDrawable(R.drawable.nhl_settings)!!
        settingsIcon.setTint(Color.parseColor(nhlPreferences.color80()))
        toolbar.setImageDrawable(settingsIcon)
        val drawableToolbar = GradientDrawable()
        drawableToolbar.cornerRadius = 100f
        drawableToolbar.setStroke(8, Color.parseColor(nhlPreferences.color50()))
        toolbar.background = drawableToolbar
        drawableSearchIcon = GradientDrawable()
        drawableSearchIcon.cornerRadius = 100f
        drawableSearchIcon.setStroke(8, Color.parseColor(nhlPreferences.color50()))
        searchIcon.background = drawableSearchIcon
        val drawableRollCategories = GradientDrawable()
        drawableRollCategories.cornerRadius = 100f
        drawableRollCategories.setStroke(8, Color.parseColor(nhlPreferences.color50()))
        rollCategoriesLayout.background = drawableRollCategories
        val drawableSearchEditText = GradientDrawable()
        drawableSearchEditText.cornerRadius = 100f
        drawableSearchEditText.setColor(Color.parseColor(nhlPreferences.color50()))
        drawableSearchEditText.setStroke(8, Color.parseColor(nhlPreferences.color50()))

        // Setup animations
        val roll = AnimationUtils.loadAnimation(this@MainActivity, R.anim.roll)
        rollOut = AnimationUtils.loadAnimation(this@MainActivity, R.anim.roll_out)
        rollToolbar = AnimationUtils.loadAnimation(this@MainActivity, R.anim.roll_toolbar)
        rollOutToolbar = AnimationUtils.loadAnimation(this@MainActivity, R.anim.roll_out_toolbar)
        searchIcon.setOnClickListener {
            VibrationUtils.vibrate(this@MainActivity, 10)

            if (searchEditText.visibility == View.VISIBLE) {
                closeSearchBar()
            } else {
                // Disable things
                disableWhileAnimation(toolbar)
                disableWhileAnimation(rollCategoriesLayout)
                disableWhileAnimation(searchEditText)
                disableWhileAnimation(rollCategories)

                // Enable the searchEditText
                enableAfterAnimation(searchEditText)
                searchEditText.visibility = View.VISIBLE

                // Set custom drawable as background
                searchEditText.background = drawableSearchEditText

                // Start animations simultaneously
                searchEditText.startAnimation(roll)
                rollCategoriesLayout.startAnimation(rollToolbar)
                toolbar.startAnimation(rollToolbar)
                roll.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        // Not needed for this case
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        searchEditText.requestFocus() // Set focus when EditText is made visible
                        searchEditText.setSelection(searchEditText.text.length) // Move cursor to the end

                        // Show the keyboard explicitly
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        // Not needed for this case
                    }
                })
                drawableSearchIcon.setSize(10, 10)
                drawableSearchIcon.setColor(Color.parseColor(nhlPreferences.color50()))
            }
        }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {
                val projection =
                    arrayOf("CATEGORY", "NAME", nhlPreferences.language(), "CMD", "ICON", "USAGE")

                // Add search filter to query
                val selection = "NAME LIKE ? OR " + nhlPreferences.language() + " LIKE ?"
                val selectionArgs = arrayOf("%$newText%", "%$newText%")
                val orderBy = "CASE WHEN NAME LIKE '$newText%' THEN 0 ELSE 1 END, " +
                        "CASE WHEN NAME LIKE '%$newText%' THEN 0 ELSE 1 END, " +
                        "CASE WHEN " + nhlPreferences.language() + " LIKE '$newText%' THEN 0 ELSE 1 END, " +
                        "CASE WHEN " + nhlPreferences.language() + " LIKE '%$newText%' THEN 0 ELSE 1 END, " +
                        "NAME OR " + nhlPreferences.language() + " ASC"

                if (newText.isNotEmpty()) {
                    disableMenu = true

                    lifecycleScope.launch {
                        try {
                            val newItemList = withContext(Dispatchers.Default) {
                                val cursor = mDatabase.query(
                                    "TOOLS",
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                    orderBy,
                                    "50"
                                )
                                val itemList: MutableList<NHLItem> = ArrayList()

                                try {
                                    if (cursor.moveToFirst()) {
                                        // Process cursor data
                                        do {
                                            val toolCategory = cursor.getString(0)
                                            val toolName = cursor.getString(1)
                                            val toolDescription = cursor.getString(2)
                                            val toolCmd = cursor.getString(3)
                                            val toolIcon = cursor.getString(4)
                                            val toolUsage = cursor.getInt(5)
                                            val item = NHLItem(
                                                toolCategory,
                                                toolName,
                                                toolDescription,
                                                toolCmd,
                                                toolIcon,
                                                toolUsage
                                            )
                                            itemList.add(item)
                                        } while (cursor.moveToNext())
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    cursor.close()
                                }

                                itemList
                            }

                            if (newItemList.isEmpty()) {
                                adapter.updateData(ArrayList()) // Empty list to clear existing data
                                enableAfterAnimation(noToolsText)
                                noToolsText.text =
                                    resources.getString(R.string.cant_found) + newText
                            } else {
                                noToolsText.text = null
                                disableWhileAnimation(noToolsText)
                                adapter.updateData(newItemList)

                                recyclerView.viewTreeObserver.addOnPreDrawListener(object :
                                    ViewTreeObserver.OnPreDrawListener {
                                    override fun onPreDraw(): Boolean {
                                        recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                                        recyclerView.scrollToPosition(0)
                                        return true
                                    }
                                })
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Prevent newlines from being entered
                    if (newText.toString().contains("\n")) {
                        val filteredText = newText.toString().replace("\n", "")
                        searchEditText.setText(filteredText)
                        searchEditText.setSelection(filteredText.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        toolbar.setOnClickListener {
            lifecycleScope.launch {
                VibrationUtils.vibrate(this@MainActivity, 10)
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                val animationBundle = ActivityOptions.makeCustomAnimation(
                    this@MainActivity,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear // Exit animation
                ).toBundle()
                startActivity(intent, animationBundle)
            }
        }

        // Start papysz easteregg
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)
        val targetTime = "21:37"

        // Compare the current time with the target time
        if (formattedTime == targetTime) {
            val handler = Handler(Looper.getMainLooper())
            lifecycleScope.launch {
                // Call the suspend function within a coroutine
                adapter.startPapysz()
            }

            ToastUtils.showCustomToast(this, "21:37")
            handler.postDelayed(stopPapysz, 5000) // show papysz face for 5s
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
//        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    fun disableWhileAnimation(v: View?) {
        runOnUiThread {
            v!!.isEnabled = false
            v.visibility = View.GONE
        }
    }

    fun enableAfterAnimation(v: View?) {
        runOnUiThread {
            v!!.isEnabled = true
            v.visibility = View.VISIBLE
        }
    }

    // Close database on app close
    override fun onDestroy() {
        super.onDestroy()
        mDatabase.close()
//        NHLManager.getInstance().shutdownExecutorService()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    R.anim.cat_appear,
                    R.anim.cat_appear
                )
            } else {
                overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear)
            }
        }
    }

    // Close searchbar or categories if back button pressed
    private fun closeOpen() {
        if (searchEditText.visibility == View.VISIBLE) {
            closeSearchBar()
        }
        if (categoriesLayout.visibility == View.VISIBLE && searchEditText.visibility == View.GONE) {
            backButton.callOnClick()
        }
    }

    private fun closeSearchBar() {
        // Clear searchbar, every close
        searchEditText.text = null

        // Enable things
        enableAfterAnimation(toolbar)
        enableAfterAnimation(rollCategoriesLayout)
        enableAfterAnimation(rollCategories)
        enableAfterAnimation(searchEditText)

        // Close the keyboard if it's open
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

        // Disable things
        disableWhileAnimation(searchEditText)
        drawableSearchIcon.setColor(Color.TRANSPARENT)

        // After animation
        rollCategoriesLayout.startAnimation(rollOutToolbar)
        toolbar.startAnimation(rollOutToolbar)
        searchEditText.startAnimation(rollOut)
        mainUtils.restartSpinner()
    }

    fun changeCategoryPreview(position: Int) {
        val categoryTextView: String = valuesList[position]
        val imageResourceId: Int = imageList[position]

        // Set image resource and color filter
        rollCategories.setImageResource(imageResourceId)
        rollCategories.setColorFilter(
            Color.parseColor(nhlPreferences.color80()),
            PorterDuff.Mode.MULTIPLY
        )

        // Set text and text color
        rollCategoriesText.text = categoryTextView
        rollCategoriesText.setTextColor(Color.parseColor(nhlPreferences.color80()))
    }

    private fun resetRecyclerHeight() {
        val prefs = getSharedPreferences("nhlSettings", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("recyclerHeight", 0)
        editor.apply()
    }

    companion object {
        @JvmField
        var disableMenu = false
    }
}