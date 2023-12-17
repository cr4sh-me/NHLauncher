package com.cr4sh.nhlauncher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class WPSAttack extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private WifiManager wifiManager;
    private LinearLayout buttonContainer; // Container for dynamic buttons
    private BroadcastReceiver wifiScanReceiver;
    private Button scanButton;
    TextView msg;
    MyPreferences myPreferences;

    private Handler countdownHandler; // Add this line
    int scanCount = 0;
    private static final int SCAN_LIMIT = 4;
    private static final int COUNTDOWN_DURATION = 120; // in seconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wps_attack_layout);

        msg = findViewById(R.id.messageBox);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        buttonContainer = findViewById(R.id.buttonContainer);

        myPreferences = new MyPreferences(this);

        msg.setTextColor(Color.parseColor(myPreferences.color80()));

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

        cancelButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
        cancelButton.setTextColor(Color.parseColor(myPreferences.color50()));

        enableScanButton(true);

        scanButton.setOnClickListener(v -> {
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

                    // Check if Wi-Fi scan throttling is enabled
//                    boolean isThrottleEnabled = Settings.Global.getInt(
//                            getContentResolver(), "wifi_scan_throttle_enabled", 1) == 1;

                    if (success ) {
                        handleScanResults();
                    }
                } finally {
                    // Enable the scan button after receiving scan results or handling throttling
                    enableScanButton(true);
                }
            }
        };


        // Register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
//
//        countdownHandler = new Handler();
//        countdownRunnable = () -> {
//            scanCountdown(); // Decrease countdown and update UI
//            countdownHandler.postDelayed(countdownRunnable, 1000);
//        };

        // Set the default message
        setMessage("Please run scan.");
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
        setMessage("Please run scan.");
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
            layoutParams.setMargins(0, 0, 0, 15); // Update the bottom margin to provide spacing between buttons
            wifiButton.setLayoutParams(layoutParams);

            // Add the click listener to the button
            wifiButton.setOnClickListener(v -> {
                // Handle the button click
                showToast("SSID: " + result.SSID + "\nWPS: " +
                        (result.capabilities != null && result.capabilities.contains("WPS")));
            });

            // Add the button to the container
            buttonContainer.addView(wifiButton);
        }
    }

    private void scanFailure() {
        buttonContainer.removeAllViews();
        setMessage("Scan failed! Please try again...");
    }

    private void showToast(String message) {
        // Display a toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        if (scanCount < SCAN_LIMIT) {
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
                setMessage("Location services are disabled. Please enable them to scan for networks.");
            }
        } else {
            // Reached scan limit, disable the scan button
            enableScanButton(false);
            // Display countdown message
//            setMessage("Scan limit reached. Countdown: " + formatTime(COUNTDOWN_DURATION));
            // Start the countdown
            startCountdown();
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
            setMessage("Please run scan.");
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
        msg.setVisibility(View.VISIBLE);
        msg.setText(message);
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
