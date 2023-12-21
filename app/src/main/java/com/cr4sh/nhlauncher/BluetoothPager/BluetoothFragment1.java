package com.cr4sh.nhlauncher.BluetoothPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.bridge.Bridge;
import com.cr4sh.nhlauncher.utils.ShellExecuter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class BluetoothFragment1 extends Fragment {

    final ShellExecuter exe = new ShellExecuter();
    @SuppressLint("SdCardPath")
    private final String APP_SCRIPTS_PATH = "/data/data/com.offsec.nethunter/scripts";
    private final String CHROOT_PATH = "/data/local/nhsystem/kali-arm64";
    MyPreferences myPreferences;

    private ProgressDialog loadingDialog;
//    private TextView Binderstatus;
//    private TextView DBUSstatus;
//    private TextView BTstatus;
//    private TextView HCIstatus;
//    private Button bluebinderButton;
//    private Button dbusButton;
//    private Button btButton;
//    private Button hciButton;
//    private File vhci;
//    private  File hwbinder;
//    private File bt_smd;
//    private String selected_iface = "";
//    private LinearLayout buttonContainer;
//    private Button refreshButton;

    private Button binderButton;
    private Button servicesButton;
    private Spinner ifaces;

    private String selected_iface;
    public BluetoothFragment1() {
        // Required empty public constructor
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bt_layout1, container, false);

        myPreferences = new MyPreferences(requireActivity());

//        buttonContainer = view.findViewById(R.id.ifaces_layout);

        TextView title = view.findViewById(R.id.bt_info);
        TextView description = view.findViewById(R.id.bt_info2);
        TextView interfacesText = view.findViewById(R.id.interfacesText);
        TextView servicesText = view.findViewById(R.id.servicesText);

        binderButton = view.findViewById(R.id.bluebinderButton);
        servicesButton = view.findViewById(R.id.btServicesButton);
        ifaces = view.findViewById(R.id.hci_interface);

//        TextView Binder = view.findViewById(R.id.bluebinder);
//        TextView DBUS = view.findViewById(R.id.dbus);
//        TextView BT = view.findViewById(R.id.bt_service);
//        TextView HCI = view.findViewById(R.id.hci);
//
//        Binderstatus = view.findViewById(R.id.BinderStatus);
//        DBUSstatus = view.findViewById(R.id.DBUSstatus);
//        BTstatus = view.findViewById(R.id.BTstatus);
//        HCIstatus = view.findViewById(R.id.HCIstatus);
//        bluebinderButton = view.findViewById(R.id.bluebinder_button);
//        dbusButton = view.findViewById(R.id.dbus_button);
//        btButton = view.findViewById(R.id.bt_button);
//        hciButton = view.findViewById(R.id.hci_button);
//        refreshButton = view.findViewById(R.id.bounce);

        title.setTextColor(Color.parseColor(myPreferences.color80()));
        description.setTextColor(Color.parseColor(myPreferences.color80()));
        interfacesText.setTextColor(Color.parseColor(myPreferences.color80()));
        servicesText.setTextColor(Color.parseColor(myPreferences.color80()));

//        Binder.setTextColor(Color.parseColor(myPreferences.color80()));
//        DBUS.setTextColor(Color.parseColor(myPreferences.color80()));
//        BT.setTextColor(Color.parseColor(myPreferences.color80()));
//        HCI.setTextColor(Color.parseColor(myPreferences.color80()));
//
//        Binderstatus.setTextColor(Color.parseColor(myPreferences.color80()));
//        DBUSstatus.setTextColor(Color.parseColor(myPreferences.color80()));
//        BTstatus.setTextColor(Color.parseColor(myPreferences.color80()));
//        HCIstatus.setTextColor(Color.parseColor(myPreferences.color80()));
//
        binderButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        binderButton.setTextColor(Color.parseColor(myPreferences.color80()));

        servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        servicesButton.setTextColor(Color.parseColor(myPreferences.color80()));
//
//        btButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
//        btButton.setTextColor(Color.parseColor(myPreferences.color80()));
//
//        hciButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
//        hciButton.setTextColor(Color.parseColor(myPreferences.color80()));

//        bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
//        if (bt_smd.exists()) {
//            Binder.setText("Bluetooth SMD");
//        }
//        hwbinder = new File("/dev/hwbinder");
//        vhci = new File("/dev/vhci");

//        createButtons();
//        getBinderStatus();
//        getDBUSStatus();
//        getBTStatus();
//        getHCIStatus(selected_iface);
//
        loadIfaces();
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

        binderButton.setOnClickListener( v -> {
            binderAction();
        });

        servicesButton.setOnClickListener(v -> {
            btServicesAction();
        });

//
//        dbusButton.setOnClickListener(v -> {
//            dbusCMDS();
//        });
//
//        btButton.setOnClickListener(v -> {
//            btCMDS();
//        });
//
//        hciButton.setOnClickListener(v -> {
//            hciCMDS();
//        });
//
//        refreshButton.setOnClickListener(v -> {
//            createButtons();
//        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    public void getBinderStatus() {
        new Thread(() -> {
            requireActivity().runOnUiThread(() -> {
                    if (!isBinderRunning()) {
                        binderButton.setText("Start Bluebinder");
                    } else {
                        binderButton.setText("Stop Bluebinder");
                    }
                loadIfaces();
            });
        }).start();
    }

    @SuppressLint("SetTextI18n")
    public void getBtServicesStatus() {
        new Thread(() -> {

            requireActivity().runOnUiThread(() -> {
               if(!isBtServicesRunning()){
                   servicesButton.setText("Start BT Services");
               } else {
                   servicesButton.setText("Stop BT Services");
               }
           loadIfaces();
            });
        }).start();
    }

    public boolean isBinderRunning(){
        String outputHCI = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1");
        String binder_statusCMD = exe.RunAsRootOutput("pidof bluebinder");
        File bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
        if (!bt_smd.exists()) {
            return !binder_statusCMD.isEmpty();
        } else {
            return outputHCI.contains("hci0");
        }
    }

    public boolean isBtServicesRunning(){
        String dbus_statusCMD = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus status | grep dbus");
        String bt_statusCMD = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth status | grep bluetooth");
        return dbus_statusCMD.equals("dbus is running.") && bt_statusCMD.equals("bluetooth is running.");
    }

    private void showLoadingDialog(String message) {
        loadingDialog = new ProgressDialog(requireContext());
        loadingDialog.setMessage(message);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }


    private void binderAction() {
        File bluebinder = new File(CHROOT_PATH + "/usr/sbin/bluebinder");
        if (bluebinder.exists()) {
            if (!isBinderRunning()) {

                lockButtons(true);
                // Show loading dialog
                showLoadingDialog("starting bluebinder...");

                // Start bluebinder process in background
                startBinder();

                // Check the status continuously until the binder is running
                new Thread(() -> {
                    while (!isBinderRunning()) {
                        try {
                            // Add a delay to avoid constant checking
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Dismiss loading dialog on the UI thread
                    requireActivity().runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        getBinderStatus(); // Update the UI after the binder is running
                        lockButtons(false);
                    });
                }).start();
            } else {
                lockButtons(true);
                // Show loading dialog
                showLoadingDialog("stopping bluebinder...");

                // Start bluebinder process in background
                stopBinder();

                // Check the status continuously until the binder is running
                new Thread(() -> {
                    while (isBinderRunning()) {
                        try {
                            // Add a delay to avoid constant checking
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Dismiss loading dialog on the UI thread
                    requireActivity().runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        getBinderStatus(); // Update the UI after the binder is running
                        lockButtons(false);
                    });
                }).start();
            }
        } else {
            Toast.makeText(requireActivity().getApplicationContext(), "Bluebinder is not installed. Launch setup first...", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBinder(){
        File bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
        if (bt_smd.exists()) {
            new Thread(() -> {
                exe.RunAsRoot(new String[]{"svc bluetooth disable"});
                exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
                exe.RunAsRoot(new String[]{"echo 1 > " + bt_smd});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            }).start();
        } else{
            new Thread(() -> {
                exe.RunAsRoot(new String[]{"svc bluetooth disable"});
                run_cmd("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit");
            }).start();
        }
    }

    private void stopBinder(){
        File bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
        if (bt_smd.exists()) {
            new Thread(() -> {
                exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
            }).start();
        } else {
            new Thread(() -> {
                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit"});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            }).start();
        }
    }

    private void btServicesAction(){
        if (!isBtServicesRunning()) {

            lockButtons(true);
            // Show loading dialog
            showLoadingDialog("Starting BT Services...");

            // Start bluebinder process in background
            startBtServices();

            // Check the status continuously until the binder is running
            new Thread(() -> {
                while (!isBtServicesRunning()) {
                    try {
                        // Add a delay to avoid constant checking
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Dismiss loading dialog on the UI thread
                requireActivity().runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    getBtServicesStatus();
                    lockButtons(false);
                });
            }).start();
        } else {

            lockButtons(true);
            // Show loading dialog
            showLoadingDialog("Stopping BT Services...");

            // Start bluebinder process in background
            stopBtServices();

            // Check the status continuously until the binder is running
            new Thread(() -> {
                while (isBinderRunning()) {
                    try {
                        // Add a delay to avoid constant checking
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Dismiss loading dialog on the UI thread
                requireActivity().runOnUiThread(() -> {
                    // todo fix stopping binder crash and 'stopping bt services' dialog loop
                    loadingDialog.dismiss();
                    getBtServicesStatus();
                    lockButtons(false);
                });
            }).start();
        }
    }

    private void startBtServices(){
        new Thread(() -> {
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus start"});
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth start"});
        }).start();
    }

    private void stopBtServices(){
        new Thread(() -> {
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus stop"});
            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth stop"});
        }).start();
    }

    private void lockButtons(boolean value){
        binderButton.setEnabled(!value);
        servicesButton.setEnabled(!value);
        if (!value) {
            binderButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
            binderButton.setTextColor(Color.parseColor(myPreferences.color80()));
            servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
            servicesButton.setTextColor(Color.parseColor(myPreferences.color80()));
        } else {
            binderButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
            binderButton.setTextColor(Color.parseColor(myPreferences.color50()));
            servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
            servicesButton.setTextColor(Color.parseColor(myPreferences.color50()));
        }
    }

    private void loadIfaces(){
        final String[] outputHCI = {""};
        outputHCI[0] = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1");
        final ArrayList<String> hciIfaces = new ArrayList<>();
        Log.d("Ifaces", Arrays.toString(outputHCI[0].split("\n")));
        if (outputHCI[0].isEmpty()) {
            hciIfaces.add("None");
            ifaces.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, hciIfaces));
        } else {
            final String[] ifacesArray = outputHCI[0].split("\n");
            ifaces.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1, ifacesArray));
        }



    }

//    private void binderAction(){
//
//        File bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
//        File hwbinder = new File("/dev/hwbinder");
//        File vhci = new File("/dev/vhci");
//
//
//        if (!isBinderRunning()) {
//            if (!bt_smd.exists() && !hwbinder.exists() && !vhci.exists()) {
//                Toast.makeText(requireActivity(), "Internal BT not supported!", Toast.LENGTH_SHORT).show();
////                final MaterialAlertDialogBuilder confirmbuilder = new MaterialAlertDialogBuilder(requireActivity());
////                confirmbuilder.setTitle("Internal bluetooth support disabled");
////                confirmbuilder.setMessage("Your device does not support hwbinder, vhci, or bt_smd. Make sure your kernel config has the recommended drivers enabled in order to use internal bluetooth.");
////                confirmbuilder.setPositiveButton("Sure", (dialogInterface, i) -> {
////                    bluebinderButton.setEnabled(false);
////                    bluebinderButton.setTextColor(Color.parseColor("#40FFFFFF"));
////                    dialogInterface.cancel();
////                });
////                confirmbuilder.setNegativeButton("Try anyway", (dialogInterface, i) -> dialogInterface.cancel());
////                final AlertDialog alert = confirmbuilder.create();
////                alert.show();
//            } else {
//                if (bt_smd.exists()) {
//                    new Thread(() -> {
//                        exe.RunAsRoot(new String[]{"svc bluetooth disable"});
//                        exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
//                        exe.RunAsRoot(new String[]{"echo 1 > " + bt_smd});
//                        exe.RunAsRoot(new String[]{"svc bluetooth enable"});
//                    }).start();
//                }
//                else {
//                    File bluebinder = new File(CHROOT_PATH + "/usr/sbin/bluebinder");
//                    if (bluebinder.exists()) {
//                        new Thread(() -> {
//                            exe.RunAsRoot(new String[]{"svc bluetooth disable"});
//                            run_cmd_background("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit");
//                        }).start();
//                    } else {
//                        Toast.makeText(requireActivity().getApplicationContext(), "Bluebinder is not installed. Launch setup first...", Toast.LENGTH_SHORT).show();
////                            RunSetup();
//                        Log.d("FileExist", bluebinder.getAbsolutePath());
//                    }
//                }
//            }
//        } else if (isBinderRunning()) {
//            if (bt_smd.exists()) {
//                new Thread(() -> {
//                    exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
//                }).start();
//            }
//            else {
//                new Thread(() -> {
//                    Log.d("BTF", "Path: " + APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit");
//                    exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit"});
//                    exe.RunAsRoot(new String[]{"svc bluetooth enable"});
//                }).start();
//            }
//        }
//        getBinderStatus();
//    }

//    @SuppressLint("SetTextI18n")
//    public void getDBUSStatus() {
//        new Thread(() -> {
//            String dbus_statusCMD = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus status | grep dbus");
//            // Post UI updates to the main thread
//            requireActivity().runOnUiThread(() -> {
//                if (dbus_statusCMD.equals("dbus is running.")) {
//                    DBUSstatus.setText("Running");
//                    dbusButton.setText("Stop");
//                }
//                else {
//                    DBUSstatus.setText("Stopped");
//                    dbusButton.setText("Start");
//                }
//            });
//        }).start();
//    }
//
//    @SuppressLint("SetTextI18n")
//    public void getBTStatus() {
//        new Thread(() -> {
//            String bt_statusCMD = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth status | grep bluetooth");
//            // Post UI updates to the main thread
//            requireActivity().runOnUiThread(() -> {
//                if (bt_statusCMD.equals("bluetooth is running.")) {
//                    BTstatus.setText("Running");
//                    btButton.setText("Stop");
//                }
//                else {
//                    BTstatus.setText("Stopped");
//                    btButton.setText("Start");
//                }
//            });
//        }).start();
//    }
//
//    @SuppressLint("SetTextI18n")
//    public void getHCIStatus(String selectedInterface) {
//        new Thread(() -> {
//            if (!selectedInterface.isEmpty()) {
//                String hci_statusCMD = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig " + selectedInterface + " | grep 'UP RUNNING' | cut -f2 -d$'\\t'");
//                // Post UI updates to the main thread
//                requireActivity().runOnUiThread(() -> {
//                    if (hci_statusCMD.equals("UP RUNNING ")) {
//                        HCIstatus.setText("Up");
//                        hciButton.setText("Stop");
//                    } else {
//                        HCIstatus.setText("Down");
//                        hciButton.setText("Start");
//                    }
//                });
//            }
//        }).start();
//    }
//
//    public void bluebinderCMDS(){
//        if (bluebinderButton.getText().equals("Start")) {
//            if (!bt_smd.exists() && !hwbinder.exists() && !vhci.exists()) {
//                final MaterialAlertDialogBuilder confirmbuilder = new MaterialAlertDialogBuilder(requireActivity());
//                confirmbuilder.setTitle("Internal bluetooth support disabled");
//                confirmbuilder.setMessage("Your device does not support hwbinder, vhci, or bt_smd. Make sure your kernel config has the recommended drivers enabled in order to use internal bluetooth.");
//                confirmbuilder.setPositiveButton("Sure", (dialogInterface, i) -> {
//                    bluebinderButton.setEnabled(false);
//                    bluebinderButton.setTextColor(Color.parseColor("#40FFFFFF"));
//                    dialogInterface.cancel();
//                });
//                confirmbuilder.setNegativeButton("Try anyway", (dialogInterface, i) -> dialogInterface.cancel());
//                final AlertDialog alert = confirmbuilder.create();
//                alert.show();
//            } else {
//                if (bt_smd.exists()) {
//                    new Thread(() -> {
//                        exe.RunAsRoot(new String[]{"svc bluetooth disable"});
//                        exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
//                        exe.RunAsRoot(new String[]{"echo 1 > " + bt_smd});
//                        exe.RunAsRoot(new String[]{"svc bluetooth enable"});
//                    }).start();
//                }
//                else {
//                    File bluebinder = new File(CHROOT_PATH + "/usr/sbin/bluebinder");
//                    if (bluebinder.exists()) {
//                        new Thread(() -> {
//                            exe.RunAsRoot(new String[]{"svc bluetooth disable"});
//                            run_cmd_background("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit");
//                        }).start();
//                    } else {
//                        Toast.makeText(requireActivity().getApplicationContext(), "Bluebinder is not installed. Launch setup first...", Toast.LENGTH_SHORT).show();
////                            RunSetup();
//                        Log.d("FileExist", bluebinder.getAbsolutePath());
//                    }
//                }
//            }
//        } else if (bluebinderButton.getText().equals("Stop")) {
//            if (bt_smd.exists()) {
//                new Thread(() -> {
//                    exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
//                }).start();
//            }
//            else {
//                new Thread(() -> {
//                    Log.d("BTF", "Path: " + APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit");
//                    exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit"});
//                    exe.RunAsRoot(new String[]{"svc bluetooth enable"});
//                }).start();
//            }
//        }
//        getBTStatus();
//    }
//
//    private void dbusCMDS(){
//        if (dbusButton.getText().equals("Start")) {
//            new Thread(() -> {
//                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus start"});
//            }).start();
//            getDBUSStatus();
//        } else if (dbusButton.getText().equals("Stop")) {
//            new Thread(() -> {
//                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service dbus stop"});
//            }).start();
//            getDBUSStatus();
//        }
//    }
//
//    private void btCMDS(){
//        if (dbusButton.getText().equals("Stop")) {
//            if (btButton.getText().equals("Start")) {
//                new Thread(() -> {
//                    exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth start"});
//                }).start();
//                getBTStatus();
//            } else if (btButton.getText().equals("Stop")) {
//                new Thread(() -> {
//                    exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd service bluetooth stop"});
//                }).start();
//                getBTStatus();
//            }
//        } else {
//            Toast.makeText(requireActivity().getApplicationContext(), "Enable dbus service first!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void hciCMDS(){
//        if (hciButton.getText().equals("Start")) {
//            if (selected_iface.isEmpty()) {
//                Toast.makeText(requireActivity().getApplicationContext(), "No interface, please refresh or check connections!", Toast.LENGTH_SHORT).show();
//            } else {
//                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig " + selected_iface + " up noscan"});
//                getHCIStatus(selected_iface);
//            }
//        } else if (hciButton.getText().equals("Stop")) {
//            exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig " + selected_iface + " down"});
//            getHCIStatus(selected_iface);
//        }
//    }
//
    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, true);
        requireActivity().startActivity(intent);
    }

    public void run_cmd_background(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, false);
        requireActivity().startActivity(intent);
    }
//
//    @SuppressLint("SetTextI18n")
//    private void createButtons() {
//        buttonContainer.removeAllViews(); // Clear previous buttons
//
//        // ... your existing code ...
//
//        // Check available interfaces and create buttons
//        List<String> hciIfaces = checkIfaces();
//
//        for (String iface : hciIfaces) {
//            Button ifaceButton = createInterfaceButton(iface);
//            buttonContainer.addView(ifaceButton);
//        }
//    }
//
//    private List<String> checkIfaces() {
//        String outputHCI = exe.RunAsRootOutput(APP_SCRIPTS_PATH + "/bootkali custom_cmd hciconfig | grep hci | cut -d: -f1");
//        final List<String> hciIfaces = new ArrayList<>();
//
//        if (!outputHCI.isEmpty()) {
//            final String[] ifacesArray = outputHCI.split("\n");
//            hciIfaces.addAll(Arrays.asList(ifacesArray));
//        }
//
//        getBinderStatus();
//        getBTStatus();
//        getDBUSStatus();
//
//        return hciIfaces;
//    }
//
//    private Button createInterfaceButton(String iface) {
//        Button ifaceButton = new Button(requireActivity());
//
//        // Customize button properties based on the interface
//        ifaceButton.setText(iface);
//        ifaceButton.setTextColor(Color.parseColor(myPreferences.color80()));
//
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setCornerRadius(60);
//        drawable.setStroke(8, Color.parseColor(myPreferences.color80()));
//        ifaceButton.setBackground(drawable);
//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        ifaceButton.setLayoutParams(layoutParams);
//
//        ifaceButton.setOnClickListener(v -> {
//            selected_iface = iface;
//        });
//
//        return ifaceButton;
//    }
//
//    public void RunSetup() {
//        run_cmd("echo -ne \"\\033]0;BT Arsenal Setup\\007\" && clear;if [[ -f /usr/bin/hciconfig && -f /usr/bin/l2ping && " +
//                "-f /usr/bin/fang && -f /usr/bin/blueranger &&-f /usr/bin/bluelog && -f /usr/bin/sdptool && -f /usr/bin/spooftooph && -f /usr/bin/sox && -f /usr/include/bluetooth/bluetooth.h ]];then echo 'All packages are installed!'; else " +
//                "apt-get update && apt-get install bluetooth bluez bluez-tools bluez-obexd libbluetooth3 sox spooftooph libglib2.0*-dev libsystemd-dev python3-dbus python3-bleuz" +
//                "libbluetooth-dev redfang bluelog blueranger -y;fi;" +
//                "if [[ -f /usr/bin/carwhisperer && -f /usr/bin/rfcomm_scan ]];then echo 'All scripts are installed!'; else " +
//                "git clone https://github.com/yesimxev/carwhisperer-0.2 /root/carwhisperer;" +
//                "cd /root/carwhisperer;make && make install;git clone https://github.com/yesimxev/bt_audit /root/bt_audit;cd /root/bt_audit/src;make;" +
//                "cp rfcomm_scan /usr/bin/;fi;" +
//                "if [[ -f /usr/lib/libglibutil.so ]]; then echo 'Libglibutil is installed!'; else git clone https://github.com/yesimxev/libglibutil /root/libglibutil;" +
//                "cd /root/libglibutil;make && make install-dev;fi;" +
//                "if [[ -f /usr/lib/libgbinder.so ]]; then echo 'Libgbinder is installed!'; else git clone https://github.com/yesimxev/libgbinder /root/libgbinder;" +
//                "cd /root/libgbinder;make && make install-dev;fi;" +
//                "if [[ -f /usr/sbin/bluebinder ]]; then echo 'Bluebinder is installed!'; else git clone https://github.com/yesimxev/bluebinder /root/bluebinder;" +
//                "cd /root/bluebinder;make && make install;fi;" +
//                "if [[ -f /root/badbt/btk_server.py ]]; then echo 'BadBT is installed!'; else git clone https://github.com/yesimxev/badbt /root/badbt && cp /root/badbt/org.thanhle.btkbservice.conf /etc/dbus-1/system.d/;fi;" +
//                "if [[ ! \"`grep 'noplugin=input' /etc/init.d/bluetooth`\" == \"\" ]]; then echo 'Bluetooth service is patched!'; else echo 'Patching Bluetooth service..' && " +
//                "sed -i -e 's/# NOPLUGIN_OPTION=.*/NOPLUGIN_OPTION=\"--noplugin=input\"/g' /etc/init.d/bluetooth;fi; echo 'Everything is installed!' && echo '\nPress any key to continue...' && read -s -n 1 && exit ");
//    }
//
//    public void RunUpdate() {
//        run_cmd("echo -ne \"\\033]0;BT Arsenal Update\\007\" && clear;apt-get update && apt-get install bluetooth bluez bluez-tools bluez-obexd libbluetooth3 sox spooftooph " +
//                "libbluetooth-dev redfang bluelog blueranger libglib2.0*-dev libsystemd-dev python3-dbus python3-bleuz -y;if [[ -f /usr/bin/carwhisperer && -f /usr/bin/rfcomm_scan && -f /root/bluebinder && -f /root/libgbinder && -f /root/libglibutil ]];" +
//                "then cd /root/carwhisperer/;git pull && make && make install;cd /root/bluebinder/;git pull && make && make install;cd /root/libgbinder/;git pull && make && " +
//                "make install-dev;cd /root/libglibutil/;git pull && make && make install-dev;cd /root/bt_audit; git pull; cd src && make;" +
//                "cp rfcomm_scan /usr/bin/;cd /root/badbt/;git pull;fi; echo 'Done! Closing in 3secs..'; sleep 3 && exit ");
//    }

}
