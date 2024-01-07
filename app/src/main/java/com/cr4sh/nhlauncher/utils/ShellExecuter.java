package com.cr4sh.nhlauncher.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

// NetHunter commands execution class
public class ShellExecuter {

    private final static String TAG = "ShellExecuter";

    public ShellExecuter() {

    }

    public void RunAsRoot(String[] command) {
        try {
            Process process = Runtime.getRuntime().exec("su -mm");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpmd : command) {
                os.writeBytes(tmpmd + '\n');
            }
            os.writeBytes("exit\n");
            os.flush();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String RunAsRootOutput(String command) {
        StringBuilder output = new StringBuilder();
        String line;
        try {
            Process process = Runtime.getRuntime().exec("su -mm");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();

            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                output.append(line).append('\n');
            }
            /* remove the last \n */
            if (output.length() > 0)
                output = new StringBuilder(output.substring(0, output.length() - 1));
            br.close();
            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e("Shell Error:", line);
            }
            br.close();
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            Log.d(TAG, "An IOException was caught: " + e.getMessage());
        } catch (InterruptedException ex) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.getMessage());
        }
        return output.toString();
    }

    public String Executer(String command) {
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

}
