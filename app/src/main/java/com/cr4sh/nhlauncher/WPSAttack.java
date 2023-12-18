package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.cr4sh.nhlauncher.bridge.Bridge;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class WPSAttack extends AppCompatActivity {
    public CheckBox customPinCheckbox;
    private String pixieCMD = "";
    private String pixieforceCMD = "";
    private String bruteCMD = "";
    public String customPINCMD = "";
    private String customPIN = "";
    private String delayCMD = "";
    private String delayTIME = "";
    private String pbcCMD = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Button selectedButton = null; // Track the currently selected button
    private WifiManager wifiManager;
    private LinearLayout buttonContainer; // Container for dynamic buttons
    private BroadcastReceiver wifiScanReceiver;
    private Button scanButton;
    TextView msg2;
    MyPreferences myPreferences;

    private Handler countdownHandler; // Add this line
    int scanCount = 0;
    private static final int SCAN_LIMIT = 4;
    private static final int COUNTDOWN_DURATION = 120; // in seconds
    boolean isThrottleEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wps_attack_layout);

        msg2 = findViewById(R.id.messageBox2);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        buttonContainer = findViewById(R.id.buttonContainer);

        myPreferences = new MyPreferences(this);
        DialogUtils dialogUtils = new DialogUtils(getSupportFragmentManager());

        msg2.setTextColor(Color.parseColor(myPreferences.color80()));

        checkThrottling();

        if(!myPreferences.isThrottlingMessageShown() & isThrottleEnabled){
            dialogUtils.openThrottlingDialog();
        }

        if(isThrottleEnabled){
            setMessage2("Wi-Fi throttling enabled");
        } else {
            setMessage2("Wi-Fi throttling disabled");
        }

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.parseColor(myPreferences.color20()));
        Window window = this.getWindow();
        window.setStatusBarColor(Color.parseColor(myPreferences.color20()));
        window.setNavigationBarColor(Color.parseColor(myPreferences.color20()));

        setFinishOnTouchOutside(false);

        TextView title = findViewById(R.id.wps_info);
        TextView description = findViewById(R.id.wps_info2);

        title.setTextColor(Color.parseColor(myPreferences.color80()));
        description.setTextColor(Color.parseColor(myPreferences.color80()));

        scanButton = findViewById(R.id.scanButton);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button launchAttackButton = findViewById(R.id.launchAttack);

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        launchAttackButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        launchAttackButton.setTextColor(Color.parseColor(myPreferences.color80()));

        CheckBox pixieDustCheckbox = findViewById(R.id.pixie);
        CheckBox pixieForceCheckbox = findViewById(R.id.pixieforce);
        CheckBox bruteCheckbox = findViewById(R.id.brute);
        customPinCheckbox = findViewById(R.id.custompin);
        CheckBox delayCheckbox = findViewById(R.id.delay);
        CheckBox wpsButtonCheckbox = findViewById(R.id.pbc);

        pixieDustCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        pixieForceCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        bruteCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        customPinCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        delayCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));
        wpsButtonCheckbox.setTextColor(Color.parseColor(myPreferences.color80()));


        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {Color.parseColor(myPreferences.color80()), Color.parseColor(myPreferences.color80())};
        CompoundButtonCompat.setButtonTintList(pixieDustCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(pixieForceCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(bruteCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(customPinCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(delayCheckbox, new ColorStateList(states, colors));
        CompoundButtonCompat.setButtonTintList(wpsButtonCheckbox, new ColorStateList(states, colors));

        pixieDustCheckbox.setOnClickListener( v -> {
            if (pixieDustCheckbox.isChecked())
                pixieCMD = " -K";
            else
                pixieCMD = "";
        });
        pixieForceCheckbox.setOnClickListener( v -> {
            if (pixieForceCheckbox.isChecked())
                pixieforceCMD = " -F";
            else
                pixieforceCMD = "";
        });
        bruteCheckbox.setOnClickListener( v -> {
            if (bruteCheckbox.isChecked())
                bruteCMD = " -B";
            else
                bruteCMD = "";
        });
        customPinCheckbox.setOnClickListener( v -> {
            if (customPinCheckbox.isChecked()) {
                dialogUtils.openWpsCustomPinDialog();
            }
            else {
                customPINCMD = "";
                customPIN = "";
//                WPSPinLayout.setVisibility(View.GONE);
            }
        });
        delayCheckbox.setOnClickListener( v -> {
            if (delayCheckbox.isChecked()) {
                delayCMD = " -d ";
//                DelayLayout.setVisibility(View.VISIBLE);
            }
            else {
                delayCMD = "";
                delayTIME = "";
//                DelayLayout.setVisibility(View.GONE);

            }
        });
        wpsButtonCheckbox.setOnClickListener( v -> {
            if (wpsButtonCheckbox.isChecked()) {
                pbcCMD = " --pbc";
            }
            else
                pbcCMD = "";
        });



        enableScanButton(true);

        scanButton.setOnClickListener(v -> {
            checkThrottling();
            // Check for location permission before initiating the scan
            if (checkLocationPermission()) {
                startWifiScan();
            } else {
                requestLocationPermission();
            }
        });

        // Initialize cancel button
        cancelButton.setOnClickListener(v -> {
//            stopCountdown(); // Stop countdown if the cancel button is pressed
            Intent intent = new Intent(WPSAttack.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Register BroadcastReceiver
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success ) {
                        handleScanResults();
                    }
                } finally {
                    // Enable the scan button after receiving scan results or handling throttling
                    enableScanButton(true);
                }
            }
        };

        launchAttackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedButton == null){
                            Toast.makeText(WPSAttack.this, "No target selected!", Toast.LENGTH_SHORT).show();
                        } else {
//                            String ssid = extractSSID(selectedButton.getText().toString()); // Extract SSID from button text
                            String bssid = extractBSSID(selectedButton.getText().toString()); // Extract SSID from button text

                            run_cmd("python3 /sdcard/nh_files/modules/oneshot.py -b " + bssid +
                                    " -i " + "wlan0" + pixieCMD + pixieforceCMD + bruteCMD + customPINCMD + customPIN + delayCMD + delayTIME + pbcCMD);

                        }
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle the case where location permission is not granted
            return;
        }
        List<ScanResult> results = wifiManager.getScanResults();

        if (!results.isEmpty()) {
            createButtons(results);
        } else {
            // Handle the case where there are no Wi-Fi networks found
            buttonContainer.removeAllViews();
            setMessage("No Wi-Fi networks found.");
        }

        // Show the default message after displaying results
        setMessage("Ready to scan");
    }


    @SuppressLint("SetTextI18n")
    private void createButtons(List<ScanResult> results) {
        buttonContainer.removeAllViews(); // Clear previous buttons

        for (ScanResult result : results) {
            Button wifiButton = new Button(this);
            wifiButton.setText("SSID: " + result.SSID + "\nMAC: " + result.BSSID + "\nWPS: " +
                    (result.capabilities != null && result.capabilities.contains("WPS")) + "\nPower: " +
                    result.level + " dBm");
            wifiButton.setTextColor(Color.parseColor(myPreferences.color80()));

            // Set the background drawable for each button
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(60);  // You can adjust the corner radius as needed
            drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
            wifiButton.setBackground(drawable);

            // Set layout parameters for the button
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 15, 0, 0);
            wifiButton.setLayoutParams(layoutParams);

            // Add click listener to handle button selection
            wifiButton.setOnClickListener(v -> handleButtonClick(wifiButton));

            // Add the button to the container
            buttonContainer.addView(wifiButton);
        }
    }

    private void handleButtonClick(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.setTextColor(Color.parseColor(myPreferences.color80()));
            // Change the background drawable for the previously selected button
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
            selectedButton.setBackground(drawable);
        }

        // If the clicked button is the same as the selected button, deselect it
        if (selectedButton == clickedButton) {
            selectedButton = null;
        } else {
            // Set the text and background color for the clicked button to indicate selection
            clickedButton.setTextColor(Color.parseColor(myPreferences.color50()));
            GradientDrawable selectedDrawable = new GradientDrawable();
            selectedDrawable.setCornerRadius(60);
            selectedDrawable.setStroke(8, Color.parseColor(myPreferences.color50()));
            clickedButton.setBackground(selectedDrawable);
            selectedButton = clickedButton;
        }
    }

    private void scanFailure() {
        buttonContainer.removeAllViews();
        setMessage("Scan failed! Please try again...");
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

    private void startWifiScan() {
        // Check if Wi-Fi scan throttling is disabled
        if (!isThrottleEnabled) {
            // Wi-Fi scan throttling is enabled, do not check scanCount
            performWifiScan();
        } else {
            // Wi-Fi scan throttling is enabled, check scanCount
            if (scanCount < SCAN_LIMIT) {
                performWifiScan();
            } else {
                // Reached scan limit, disable the scan button
                enableScanButton(false);
                // Display countdown message
                // setMessage("Scan limit reached. Countdown: " + formatTime(COUNTDOWN_DURATION));
                // Start the countdown
                startCountdown();
            }
        }
    }

    private void performWifiScan() {
        scanCount++;
        // Continue with the scan
        if (isLocationEnabled()) {
            // Disable the scan button during scanning
            enableScanButton(false);
            // Show "Scanning" text
            buttonContainer.removeAllViews();
            setMessage("Scanning...");
            // Start the Wi-Fi scan
            boolean success = wifiManager.startScan();
            if (!success) {
                // Scan failure handling
                scanFailure();
                // Enable the scan button in case of scan failure
                enableScanButton(true);
            }
        } else {
            buttonContainer.removeAllViews();
            setMessage("Please enable location services first!");
        }
    }


    private void startCountdown() {
        countdownHandler = new Handler();

        final int countdownSeconds = COUNTDOWN_DURATION;

        // Update countdown message every second
        for (int i = 0; i <= COUNTDOWN_DURATION; i++) {
            int minutes = (countdownSeconds - i) / 60;
            int remainingSeconds = (countdownSeconds - i) % 60;
            String countdownMessage = String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds);

            final int delay = i * 1000; // Delay increases every second

            countdownHandler.postDelayed(() -> {
                setMessage("Scan limit reached. Countdown: " + countdownMessage);
            }, delay);
        }

        // After the countdown completes, display the default message
        countdownHandler.postDelayed(() -> {
            // Countdown completed, reset scanCount and enable the scan button
            scanCount = 0;
            enableScanButton(true);
            // Show the default message after countdown completion
            setMessage("Ready to scan");
        }, (COUNTDOWN_DURATION + 1) * 1000); // Additional delay for the default message
    }

    private void enableScanButton(boolean enabled) {
        scanButton.setEnabled(enabled);
        if (enabled) {
            scanButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
            scanButton.setTextColor(Color.parseColor(myPreferences.color80()));
        } else {
            scanButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
            scanButton.setTextColor(Color.parseColor(myPreferences.color50()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the operation
                startWifiScan();
            } else {
                // Permission denied, show a message or handle accordingly
                buttonContainer.removeAllViews();
                setMessage("Location permission denied. Cannot scan for networks.");
                // Enable the scan button after handling permission denial
                enableScanButton(true);
            }
        }
    }

    private void setMessage(String message){
        scanButton.setText(message);
//        msg.setVisibility(View.VISIBLE);
//        msg.setText(message);
    }

    private void setMessage2(String message){
        msg2.setVisibility(View.VISIBLE);
        msg2.setText(message);
    }

    private void checkThrottling() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isThrottleEnabled = wifiManager.isScanThrottleEnabled();
        } else {
            isThrottleEnabled = true;
        }
    }

    private String extractSSID(String buttonText) {
        String[] lines = buttonText.split("\n"); // Split the button text by newlines
        String ssidLine = lines[0]; // SSID is the first line
        return ssidLine.substring(ssidLine.indexOf(":") + 2); // Extract the SSID value
    }

    private String extractBSSID(String buttonText) {
        String[] lines = buttonText.split("\n"); // Split the button text by newlines
        String bssidLine = lines[1]; // BSSID is the second line
        return bssidLine.substring(bssidLine.indexOf(":") + 2); // Extract the BSSID value
    }

    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(wifiScanReceiver);
        if (countdownHandler != null) {
            countdownHandler.removeCallbacksAndMessages(null);
        }
    }
}
