package com.cr4sh.nhlauncher.bluetoothPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.bridge.Bridge;
import com.cr4sh.nhlauncher.overrides.CustomSpinnerAdapter;
import com.cr4sh.nhlauncher.utils.DialogUtils;
import com.cr4sh.nhlauncher.utils.NHLManager;
import com.cr4sh.nhlauncher.utils.NHLPreferences;
import com.cr4sh.nhlauncher.utils.ShellExecuter;
import com.cr4sh.nhlauncher.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BluetoothFragment1 extends Fragment {

    private static String selected_iface;
    private static Button selectedButton = null;
    final ShellExecuter exe = new ShellExecuter();
    @SuppressLint("SdCardPath")
    private final String APP_SCRIPTS_PATH = "/data/data/com.offsec.nethunter/scripts";
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();
    public String scanTime = "10";
    NHLPreferences NHLPreferences;
    ScrollView scrollView;
    List<Integer> imageList;
    private Handler mainHandler;
    private Button binderButton;
    private Button servicesButton;
    private Button scanButton;
    private Spinner ifaces;
    private File bt_smd;
    private File bluebinder;
    private LinearLayout buttonContainer;

    public BluetoothFragment1() {
        // Required empty public constructor
    }

    public static String getSelectedTarget() {
        return selectedButton.getText().toString();
    }

    public static String getSelectedIface() {
        return selected_iface;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bt_layout1, container, false);

        NHLPreferences = new NHLPreferences(requireActivity());
        mainHandler = new Handler(Looper.getMainLooper());
        bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
        String CHROOT_PATH = "/data/local/nhsystem/kali-arm64";
        bluebinder = new File(CHROOT_PATH + "/usr/sbin/bluebinder");

        scrollView = view.findViewById(R.id.scrollView2);
        buttonContainer = view.findViewById(R.id.buttonContainer);

        TextView description = view.findViewById(R.id.bt_info2);
        TextView interfacesText = view.findViewById(R.id.interfacesText);
        TextView servicesText = view.findViewById(R.id.servicesText);
        TextView scanText = view.findViewById(R.id.scanText);

        scanText.setTextColor(Color.parseColor(NHLPreferences.color80()));

        scanButton = view.findViewById(R.id.scanButton);
        Button scanTimeButton = view.findViewById(R.id.scanTime);

        scanButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        scanButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        scanTimeButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        scanTimeButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        binderButton = view.findViewById(R.id.bluebinderButton);
        servicesButton = view.findViewById(R.id.btServicesButton);
        ifaces = view.findViewById(R.id.hci_interface);
        LinearLayout ifacesContainer = view.findViewById(R.id.spinnerContainer);
        setContainerBackground(ifacesContainer);

        imageList = List.of(
                R.drawable.kali_wireless_attacks_trans
        );

        description.setTextColor(Color.parseColor(NHLPreferences.color80()));
        interfacesText.setTextColor(Color.parseColor(NHLPreferences.color80()));
        servicesText.setTextColor(Color.parseColor(NHLPreferences.color80()));

        binderButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        binderButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        servicesButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
        servicesButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

        try {
            loadIfaces();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        getBinderStatus();
        getBtServicesStatus();

        ifaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                selected_iface = parentView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // TODO document why this method is empty
            }
        });

        binderButton.setOnClickListener(v -> {
            try {
                lockButton(false, "Please wait...", binderButton);
                binderAction();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        servicesButton.setOnClickListener(v -> {
            try {
                lockButton(false, "Please wait...", servicesButton);
                btServicesAction();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        scanButton.setOnClickListener(v -> runBtScan());

        DialogUtils dialogUtils = new DialogUtils(requireActivity().getSupportFragmentManager());

        scanTimeButton.setOnClickListener(v -> dialogUtils.openScanTimeDialog(1, this));

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void createButtons(String[] devices) {
        buttonContainer.removeAllViews(); // Clear previous buttons

        int buttonCount = 4;
        int scrollViewHeight = scrollView.getHeight();
        int buttonPadding = 15;

        for (String device : devices) {
            Button bluetoothButton = new Button(requireActivity());

            String cleanText = device.replaceAll("[\\[\\]]", "");

            // Assume MAC address is always 17 characters
            String bluetooth_name = (cleanText.substring(18)).strip();
            String bluetooth_address = (cleanText.substring(0, 18)).strip();

            Log.d("HDH", bluetooth_name + " - " + bluetooth_address);

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            // Set bold style for BT address
            ssb.append(bluetooth_name, new StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append("\n");
            ssb.append(bluetooth_address);

            bluetoothButton.setText(ssb);
            bluetoothButton.setTextColor(Color.parseColor(NHLPreferences.color80()));

            // Set the background drawable for each button
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(60);
            drawable.setStroke(8, Color.parseColor(NHLPreferences.color80()));
            bluetoothButton.setBackground(drawable);

            // Calculate button height dynamically
            int buttonHeight = (scrollViewHeight / buttonCount) - buttonPadding;

            // Set layout parameters for the button
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    buttonHeight
            );
            layoutParams.setMargins(0, (buttonPadding / 2), 0, (buttonPadding / 2));
            bluetoothButton.setLayoutParams(layoutParams);

            // Add click listener to handle button selection
            bluetoothButton.setOnClickListener(v -> handleButtonClick(bluetoothButton));

            // Add the button to the container
            buttonContainer.addView(bluetoothButton);
        }
    }

    private void handleButtonClick(Button clickedButton) {
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

    private void runBtScan() {
        if (!selected_iface.equals("None")) {
            executor.submit(() -> {
                try {
                    Future<String> future1 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig " + selected_iface + " | grep 'UP RUNNING' | cut -f2 -d$'\\t'"));
                    String hci_current = future1.get();
                    if (!hci_current.equals("UP RUNNING ")) {
                        exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig " + selected_iface + " up"});
                    }

                    mainHandler.post(() -> {
                        buttonContainer.removeAllViews();
                        lockButton(false, "Scanning...", scanButton);
                    });

                    Future<String> future2 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hcitool -i " + selected_iface + " scan  --length " + scanTime + " | grep -A 1000 \"Scanning ...\" | awk '/Scanning .../{flag=1;next}/--/{flag=0}flag'\n"));
                    String scanOutput = future2.get();
                    Log.d("hcitool", scanOutput);

                    if (!scanOutput.isEmpty()) {
                        String[] devicesList = scanOutput.split("\n");
                        Log.d("hcitool", "not empty: " + Arrays.toString(devicesList));
                        mainHandler.post(() -> {
                            createButtons(devicesList);
                            lockButton(true, "Scan", scanButton);
                        });
                    } else {
                        mainHandler.post(() -> lockButton(true, "No devices found...", scanButton));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            ToastUtils.showCustomToast(requireActivity(), "no selected interface!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @SuppressLint("SetTextI18n")
    public void getBinderStatus() {

        executor.submit(() -> {
            try {
                boolean isBinderRunning = isBinderRunning();
                mainHandler.post(() -> lockButton(true, isBinderRunning ? "Stop Bluebinder" : "Start Bluebinder", binderButton));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        executor.submit(() -> {
            try {
                loadIfaces();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void getBtServicesStatus() {

        executor.submit(() -> {
            try {
                boolean isBtServicesRunning = isBtServicesRunning();
                mainHandler.post(() -> lockButton(true, isBtServicesRunning ? "Stop BT Services" : "Start BT Services", servicesButton));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public boolean isBinderRunning() throws ExecutionException, InterruptedException {

        Future<String> future1 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1"));

        Future<String> future2 = executor.submit(() -> exe.RunAsRootOutput("pidof bluebinder"));

        String outputHCI = future1.get();  // Get the result from the Future
        String binder_statusCMD = future2.get();  // Get the result from the Future

        if (!bt_smd.exists()) {
            return !binder_statusCMD.isEmpty();
        } else {
            return outputHCI.contains("hci0");
        }
    }

    public boolean isBtServicesRunning() throws ExecutionException, InterruptedException {
        Future<String> future1 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus status | grep dbus"));
        Future<String> future2 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth status | grep bluetooth"));

        String dbus_statusCMD = future1.get();
        String bt_statusCMD = future2.get();

        Log.d("DEBSHIT", dbus_statusCMD + " " + bt_statusCMD);

        return dbus_statusCMD.equals("dbus is running.") && bt_statusCMD.equals("bluetooth is running.");
    }

    @SuppressLint("SetTextI18n")
    private void binderAction() throws ExecutionException, InterruptedException {

        if (bluebinder.exists()) {
            executor.submit(() -> {
                try {
                    boolean isRunningBeforeAction = isBinderRunning();

                    // Check if binder is running
                    if (!isRunningBeforeAction) {
                        // Start bluebinder process in the background
                        startBinder();
                    } else {
                        // Stop bluebinder process in the background
                        stopBinder();
                    }

                    // Schedule periodic updates using ScheduledExecutorService
                    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                    scheduledExecutorService.scheduleAtFixedRate(() -> {
                        try {
                            boolean isRunningAfterAction = isBinderRunning();
                            if (isRunningBeforeAction != isRunningAfterAction) {
                                // Update button text on the UI thread
                                mainHandler.post(() -> {
                                    getBinderStatus();
                                    scheduledExecutorService.shutdown();
                                });
                            }
                        } catch (Exception e) {
                            // Log exceptions
                            Log.e("ERROR", "Exception during periodic update", e);
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    // Log exceptions
                    Log.e("ERROR", "Exception during binderAction", e);
                }
            });

        } else {
            ToastUtils.showCustomToast(requireActivity(), "Bluebinder is not installed. Launch setup first...");
        }
    }

    @SuppressLint("SetTextI18n")
    private void btServicesAction() throws ExecutionException, InterruptedException {

        // Check if BT Services are running
        boolean areServicesRunning = isBtServicesRunning();

        executor.submit(() -> {
            try {
                if (!areServicesRunning) {
                    // Start BT Services
                    startBtServices();
                } else {
                    // Stop BT Services
                    stopBtServices();
                }

                // Wait for the action to complete
                Thread.sleep(1000); // Adjust the sleep duration if needed

                // Update button text on the UI thread
                mainHandler.post(() -> {
                    Log.d("DEBUG", "Update on UI thread");
                    getBtServicesStatus();
                });
            } catch (Exception e) {
                // Log exceptions
                Log.e("ERROR", "Exception during btServicesAction", e);
            }
        });

    }

    private void startBinder() {
        if (bt_smd.exists()) {
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{"svc bluetooth disable"});
                exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
                exe.RunAsRoot(new String[]{"echo 1 > " + bt_smd});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            });
        } else {
            executor.execute(() -> exe.RunAsRoot(new String[]{"svc bluetooth disable"}));
            run_cmd("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit");
        }
    }

    private void stopBinder() {
        if (bt_smd.exists()) {
            executor.execute(() -> exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd}));
        } else {
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit"});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            });
        }
    }

    private void startBtServices() {
        executor.submit(() -> {
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus start"});
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth start"});
        });
    }

    private void stopBtServices() {
        executor.submit(() -> {
            // Stop BT services
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth stop"});
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus stop"});

            Log.d("BT Services", "Stop operation completed");
        });
    }

    private void lockButton(boolean value, String binderButtonText, Button choosenButton) {
        mainHandler.post(() -> {
            choosenButton.setEnabled(value);
            choosenButton.setText(binderButtonText);

            if (value) {
                choosenButton.setBackgroundColor(Color.parseColor(NHLPreferences.color50()));
                choosenButton.setTextColor(Color.parseColor(NHLPreferences.color80()));
            } else {
                choosenButton.setBackgroundColor(Color.parseColor(NHLPreferences.color80()));
                choosenButton.setTextColor(Color.parseColor(NHLPreferences.color50()));
            }
        });
    }

    private void loadIfaces() throws ExecutionException, InterruptedException {
        final String[] outputHCI = {""};
        Future<String> future1 = executor.submit(() -> exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1"));
        outputHCI[0] = future1.get();
        final ArrayList<String> hciIfaces = new ArrayList<>();
        Log.d("Ifaces", Arrays.toString(outputHCI[0].split("\n")));
        if (outputHCI[0].isEmpty()) {
            mainHandler.post(() -> {
                hciIfaces.add("None");
                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireActivity(), hciIfaces, imageList, NHLPreferences.color20(), NHLPreferences.color80());
                ifaces.setAdapter(customSpinnerAdapter);
            });
        } else {
            final String[] ifacesArray = outputHCI[0].split("\n");
            mainHandler.post(() -> {
                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireActivity(), List.of(ifacesArray), imageList, Objects.requireNonNull(NHLPreferences.color20()), NHLPreferences.color80());
                ifaces.setAdapter(customSpinnerAdapter);
            });
        }
    }

    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.Companion.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, false);
        requireActivity().startActivity(intent);
    }

    public void run_cmd_background(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.Companion.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, true);
        requireActivity().startActivity(intent);
    }

    private void setContainerBackground(LinearLayout container) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(60);
        drawable.setStroke(8, Color.parseColor(NHLPreferences.color50()));
        container.setBackground(drawable);
    }

}