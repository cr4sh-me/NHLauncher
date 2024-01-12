package com.cr4sh.nhlauncher.WpsAttacks;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.SpecialFeatures.SpecialFeaturesActivity;
import com.cr4sh.nhlauncher.bridge.Bridge;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ShellExecuter;
import com.cr4sh.nhlauncher.utils.ToastUtils;
import com.cr4sh.nhlauncher.utils.VibrationUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class WPSAttack extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public String customPINCMD = "";
    public String delayCMD = "";
    TextView msg2;
    NHLPreferences NHLPreferences;
    boolean isThrottleEnabled;
    private String pixieCMD = "";
    private String pixieforceCMD = "";
    private String bruteCMD = "";
    private String pbcCMD = "";
    private Button selectedButton = null; // Track the currently selected button
    private WifiManager wifiManager;
    private LinearLayout buttonContainer; // Container for dynamic buttons
    private BroadcastReceiver wifiScanReceiver;
    private Button scanButton;
    private final ExecutorService executorService = NHLManager.getInstance().getExecutorService();

    private ShellExecuter exe;
    private static String extractBSSID(String buttonText) {
        String[] lines = buttonText.split("\n");
        return lines[1].trim();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.cat_appear, R.anim.cat_appear);
        } else {
            overridePendingTransition(R.anim.cat_appear, R.anim.cat_disappear);
        }
        exe = new ShellExecuter();

        setContentView(R.layout.wps_attack_layout);

        msg2 = findViewById(R.id.messageBox2);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        buttonContainer = findViewById(R.id.buttonContainer);

        NHLPreferences = new NHLPreferences(this);
        DialogUtils dialogUtils = new DialogUtils(getSupportFragmentManager());

        msg2.setTextColor(Color.parseColor(NHLPreferences.color80()));

        try {
            checkThrottling();
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (NHLPreferences.isThrottlingMessageShown() & isThrottleEnabled) {
            dialogUtils.openThrottlingDialog();
        }

        if (isThrottleEnabled) {
            setMessage2("Wi-Fi throttling enabled");
        } else {
            setMessage2("Wi-Fi throttling disabled");
        }

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(NHLPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(NHLPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(NHLPreferences.color20()));

        setFinishOnTouchOutside(false);

        LinearLayout choiceContainer = findViewById(R.id.choiceContainer);
        GradientDrawable selectedDrawable = new GradientDrawable();
        selectedDrawable.setCornerRadius(60);
        selectedDrawable.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        choiceContainer.setBackground(selectedDrawable);

        TextView title = findViewById(R.id.wps_info);
        TextView description = findViewById(R.id.wps_info2);

        title.setTextColor(Color.parseColor(NHLPreferences.color80()));
        description.setTextColor(Color.parseColor(NHLPreferences.color80()));

        scanButton = findViewById(R.id.scanButton);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button launchAttackButton = findViewById(R.id.launchAttack);

        cancelButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(NHLPreferences.color50()));

        launchAttackButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        launchAttackButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        CheckBox pixieDustCheckbox = findViewById(R.id.pixie);
        CheckBox pixieForceCheckbox = findViewById(R.id.pixieforce);
        CheckBox bruteCheckbox = findViewById(R.id.brute);
        Button customPinCheckbox = findViewById(R.id.custompin);
        Button delayCheckbox = findViewById(R.id.delay);
        CheckBox wpsButtonCheckbox = findViewById(R.id.pbc);

        customPinCheckbox.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        customPinCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));

        delayCheckbox.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        delayCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));


        pixieDustCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));
        pixieForceCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));
        bruteCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));
        wpsButtonCheckbox.setTextColor(Color.parseColor(NHLPreferences.color80()));

        pixieDustCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));
        pixieForceCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));
        bruteCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));
        wpsButtonCheckbox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(NHLPreferences.color80())));

        pixieDustCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            if (pixieDustCheckbox.isChecked())
                pixieCMD = " -K";
            else
                pixieCMD = "";
        });
        pixieForceCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            if (pixieForceCheckbox.isChecked())
                pixieforceCMD = " -F";
            else
                pixieforceCMD = "";
        });
        bruteCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            if (bruteCheckbox.isChecked())
                bruteCMD = " -B";
            else
                bruteCMD = "";
        });
        customPinCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            dialogUtils.openWpsCustomSetting(1, WPSAttack.this);
        });
        delayCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            dialogUtils.openWpsCustomSetting(2, WPSAttack.this);
        });

        wpsButtonCheckbox.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            if (wpsButtonCheckbox.isChecked()) {
                pbcCMD = " --pbc";
            } else
                pbcCMD = "";
        });

        enableScanButton(true);

        scanButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            try {
                checkThrottling();
            } catch (Settings.SettingNotFoundException e) {
                throw new RuntimeException(e);
            }
            // Check for location permission before initiating the scan
            if (checkLocationPermission()) {
                if (!wifiManager.isWifiEnabled()) {
                    enableScanButton(false);
                    setMessage("Enabling WiFi...");
                    enableWifi();
                    new Handler().postDelayed(this::performWifiScan, 3000);
                } else {
                    performWifiScan();
                }
            } else {
                requestLocationPermission();
            }
        });

        // Initialize cancel button
        cancelButton.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            Intent intent = new Intent(WPSAttack.this, SpecialFeaturesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle animationBundle = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.cat_appear,  // Enter animation
                    R.anim.cat_disappear  // Exit animation
            ).toBundle();
            startActivity(intent, animationBundle);
            finish();
        });

        msg2.setOnClickListener(v -> {
            VibrationUtils.vibrate(this, 10);
            if (isThrottleEnabled){
                dialogUtils.openThrottlingDialog();
            }
        });

        // Register BroadcastReceiver
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    handleScanResults();
                }
            }
        };

        launchAttackButton.setOnClickListener(
                v -> {
                    VibrationUtils.vibrate(this, 10);
                    if (selectedButton == null) {
                        ToastUtils.showCustomToast(this, "No target selected!");
                    } else {
                        wifiManager.disconnect(); // disconnect from active ap to prevent issues
                        String bssid = extractBSSID(selectedButton.getText().toString()); // Extract SSID from button text
                        run_cmd("python3 /sdcard/nh_files/modules/oneshot.py -b " + bssid +
                                " -i " + "wlan0" + pixieCMD + pixieforceCMD + bruteCMD + customPINCMD + delayCMD + pbcCMD);
                    }
                }
        );


        // Register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        // Set the default message
        setMessage("Ready to scan");
    }

    private void handleScanResults() {
        // Retrieve the scan results
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            List<ScanResult> results = wifiManager.getScanResults();
            if (!results.isEmpty()) {
                Log.d("ResultsScan", "results found : " + results);
                // Check if there are any WPS networks
                boolean hasWpsNetworks = false;
                for (ScanResult result : results) {
                    if (result.capabilities != null && result.capabilities.contains("WPS")) {
                        hasWpsNetworks = true;
                        break;  // Break out of the loop since we found at least one WPS network
                    }
                }
                if (hasWpsNetworks) {
                    // There is at least one WPS network in the results
                    createButtons(results);
                    setMessage("Ready to scan");
                } else {
                    // Handle the case where there are no WPS networks found
                    buttonContainer.removeAllViews();
                    setMessage("No WPS networks found!");
                }
            } else {
                // Handle the case where there are no Wi-Fi networks found

                Log.d("ResultsScan", "results not found : " + results);

                buttonContainer.removeAllViews();
                setMessage("No WiFi networks found!");
            }
            enableScanButton(true);
        } catch (Exception e) {
            ToastUtils.showCustomToast(this, "E: " + e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void createButtons(List<ScanResult> results) {
        buttonContainer.removeAllViews(); // Clear previous buttons

        int buttonCount = 4;
        ScrollView scrollView = findViewById(R.id.scrollView2);
        int scrollViewHeight = scrollView.getHeight();
        int buttonPadding = 15;

        // Sort the results by signal strength in descending order
        results.sort((result1, result2) -> Integer.compare(result2.level, result1.level));

        for (ScanResult result : results) {
            if (result.capabilities != null && result.capabilities.contains("WPS")) {
                Button wifiButton = new Button(this);

                // Create a SpannableStringBuilder to apply styles to different parts of the text
                SpannableStringBuilder ssb = new SpannableStringBuilder();

                // Set bold style for SSID
                String ssidText = result.SSID;
                ssb.append(ssidText, new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append("\n");
                ssb.append(result.BSSID);
                ssb.append("\n");
                ssb.append(String.valueOf(result.level)).append(" dBm");

                wifiButton.setText(ssb);
                wifiButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

                // Set the background drawable for each button
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(60);
                drawable.setStroke(8, Color.parseColor(NHLPreferences.color80()));
                wifiButton.setBackground(drawable);

                // Calculate button height dynamically
                int buttonHeight = (scrollViewHeight / buttonCount) - buttonPadding;

                // Set layout parameters for the button
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        buttonHeight
                );
                layoutParams.setMargins(0, (buttonPadding / 2), 0, (buttonPadding / 2));
                wifiButton.setLayoutParams(layoutParams);

                // Add click listener to handle button selection
                wifiButton.setOnClickListener(v -> handleButtonClick(wifiButton));

                // Add the button to the container
                buttonContainer.addView(wifiButton);
            }
        }
    }

    private void handleButtonClick(Button clickedButton) {
        VibrationUtils.vibrate(this, 10);
        if (selectedButton != null) {
            selectedButton.setTextColor(Color.parseColor(NHLPreferences.color80()));
            // Change the background drawable for the previously selected button
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(NHLPreferences.color80()));
            selectedButton.setBackground(drawable);
        }

        // If the clicked button is the same as the selected button, deselect it
        if (selectedButton == clickedButton) {
            selectedButton = null;
        } else {
            // Set the text and background color for the clicked button to indicate selection
            clickedButton.setTextColor(Color.parseColor(NHLPreferences.color50()));
            GradientDrawable selectedDrawable = new GradientDrawable();
            selectedDrawable.setCornerRadius(60);
            selectedDrawable.setStroke(8, Color.parseColor(NHLPreferences.color50()));
            clickedButton.setBackground(selectedDrawable);
            selectedButton = clickedButton;
        }
    }

    private boolean checkLocationPermission() {
        // Check if the ACCESS_FINE_LOCATION permission is granted
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        // Request the ACCESS_FINE_LOCATION permission if not granted
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean isLocationEnabled() {
        int mode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private void performWifiScan() {
        if (isLocationEnabled()) {
            // Start the Wi-Fi scan
            try {
                boolean success = wifiManager.startScan();
                if (!success && isThrottleEnabled) {
                    enableScanButton(false);
                    setMessage("Scan limit reached, re-enabling WiFi...");
                    resetWifi();
                    new Handler().postDelayed(this::performWifiScan, 5000);
                } else {
                    enableScanButton(false);
                    buttonContainer.removeAllViews();
                    setMessage("Scanning...");
                }
            } catch (Exception e) {
                buttonContainer.removeAllViews();
                ToastUtils.showCustomToast(this, "E: " + e.getMessage());
            }
        } else {
            buttonContainer.removeAllViews();
            setMessage("Please enable location services first!");
        }
    }

    private void enableScanButton(boolean enabled) {
        scanButton.setEnabled(enabled);
        if (enabled) {
            scanButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
            scanButton.setTextColor(Color.parseColor(NHLPreferences.color80()));
        } else {
            scanButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
            scanButton.setTextColor(Color.parseColor(NHLPreferences.color50()));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission denied, show a message or handle accordingly
                buttonContainer.removeAllViews();
                setMessage("Location permission denied. Cannot scan for networks.");
            }
        }
    }

    private void setMessage(String message) {
        scanButton.setText(message);
    }

    private void setMessage2(String message) {
        msg2.setText(message);
    }

    private void checkThrottling() throws Settings.SettingNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isThrottleEnabled = wifiManager.isScanThrottleEnabled();
        } else {
            //based on https://stackoverflow.com/a/61147099 answer
            int enabledInt = Settings.Global.getInt(this.getContentResolver(), "wifi_scan_throttle_enabled");
            isThrottleEnabled = enabledInt != 1;
        }
    }

    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, true);
        startActivity(intent);
    }

    private void enableWifi(){
        executorService.submit(()-> exe.RunAsRoot(new String[]{"svc wifi enable"}));
    }

    private void resetWifi() {
        executorService.submit(() -> exe.RunAsRoot(new String[]{"svc wifi disable"}));
        new Handler().postDelayed(() -> executorService.submit(() -> exe.RunAsRoot(new String[]{"svc wifi enable"})), 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(wifiScanReceiver);
//        NHLManager.getInstance().shutdownExecutorService();
    }
}