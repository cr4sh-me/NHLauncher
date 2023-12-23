package com.cr4sh.nhlauncher.BluetoothPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.bridge.Bridge;
import com.cr4sh.nhlauncher.utils.ShellExecuter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BluetoothFragment1 extends Fragment {

    final ShellExecuter exe = new ShellExecuter();
    @SuppressLint("SdCardPath")
    private final String APP_SCRIPTS_PATH = "/data/data/com.offsec.nethunter/scripts";
    MyPreferences myPreferences;
    private Button binderButton;
    private Button servicesButton;
    private Spinner ifaces;
    private String selected_iface;
    String CHROOT_PATH = "/data/local/nhsystem/kali-arm64";
    File bt_smd;
    File bluebinder;

    private ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public BluetoothFragment1() {
        // Required empty public constructor
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bt_layout1, container, false);

        executor = Executors.newCachedThreadPool();
        bt_smd = new File("/sys/module/hci_smd/parameters/hcismd_set");
        bluebinder = new File(CHROOT_PATH + "/usr/sbin/bluebinder");

        myPreferences = new MyPreferences(requireActivity());

        TextView title = view.findViewById(R.id.bt_info);
        TextView description = view.findViewById(R.id.bt_info2);
        TextView interfacesText = view.findViewById(R.id.interfacesText);
        TextView servicesText = view.findViewById(R.id.servicesText);
        Button scanButton = view.findViewById(R.id.scanButton);

        scanButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        scanButton.setTextColor(Color.parseColor(myPreferences.color80()));


        binderButton = view.findViewById(R.id.bluebinderButton);
        servicesButton = view.findViewById(R.id.btServicesButton);
        ifaces = view.findViewById(R.id.hci_interface);

        title.setTextColor(Color.parseColor(myPreferences.color80()));
        description.setTextColor(Color.parseColor(myPreferences.color80()));
        interfacesText.setTextColor(Color.parseColor(myPreferences.color80()));
        servicesText.setTextColor(Color.parseColor(myPreferences.color80()));

        binderButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        binderButton.setTextColor(Color.parseColor(myPreferences.color80()));

        servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        servicesButton.setTextColor(Color.parseColor(myPreferences.color80()));

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

        binderButton.setOnClickListener( v -> {
            try {
                lockBinderButtons(false, "Please wait...");
                binderAction();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        servicesButton.setOnClickListener(v -> {
            try {
                lockServicesButton(false, "Please wait...");
                btServicesAction();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        return view;
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
                mainHandler.post(() -> {
                    lockBinderButtons(true, isBinderRunning ? "Stop Bluebinder" : "Start Bluebinder");
                });
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
                mainHandler.post(() -> {
                    lockServicesButton(true, isBtServicesRunning ? "Stop BT Services" : "Start BT Services");
                });
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
            Toast.makeText(requireActivity().getApplicationContext(), "Bluebinder is not installed. Launch setup first...", Toast.LENGTH_SHORT).show();
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

    private void startBinder(){
        if (bt_smd.exists()) {
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{"svc bluetooth disable"});
                exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
                exe.RunAsRoot(new String[]{"echo 1 > " + bt_smd});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            });
        } else{
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{"svc bluetooth disable"});
            });
            run_cmd("echo -ne \"\\033]0;Bluebinder\\007\" && clear;bluebinder || bluebinder;exit");
        }
    }

    private void stopBinder(){
        if (bt_smd.exists()) {
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{"echo 0 > " + bt_smd});
            });
        } else {
            executor.execute(() -> {
                exe.RunAsRoot(new String[]{APP_SCRIPTS_PATH + "/bootkali custom_cmd pkill bluebinder;exit"});
                exe.RunAsRoot(new String[]{"svc bluetooth enable"});
            });
        }
    }
    private void startBtServices(){
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

    private void lockBinderButtons(boolean value, String binderButtonText) {
        mainHandler.post(() -> {
            binderButton.setText(binderButtonText);
            binderButton.setEnabled(value);

            if (value) {
                binderButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
                binderButton.setTextColor(Color.parseColor(myPreferences.color80()));
            } else {
                binderButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
                binderButton.setTextColor(Color.parseColor(myPreferences.color50()));
            }
        });
    }

    private void lockServicesButton(boolean value, String servicesButtonText) {
        mainHandler.post(() -> {
            servicesButton.setText(servicesButtonText);
            servicesButton.setEnabled(value);

            if (value) {
                servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color50()));
                servicesButton.setTextColor(Color.parseColor(myPreferences.color80()));
            } else {
                servicesButton.setBackgroundColor(Color.parseColor(myPreferences.color80()));
                servicesButton.setTextColor(Color.parseColor(myPreferences.color50()));
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
                ifaces.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, hciIfaces));
            });
        } else {
            final String[] ifacesArray = outputHCI[0].split("\n");
            mainHandler.post(() -> {
                ifaces.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1, ifacesArray));
            });
        }
    }

    public void run_cmd(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, true);
        requireActivity().startActivity(intent);
    }

    public void run_cmd_background(String cmd) {
        @SuppressLint("SdCardPath") Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd, false);
        requireActivity().startActivity(intent);
    }



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