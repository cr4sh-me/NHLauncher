package com.cr4sh.nhlauncher.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ShellExecuter {

    private SimpleDateFormat timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final static String TAG = "ShellExecuter";

    public ShellExecuter() {

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

    public String Executer(String[] command) {
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

    public String RunAsRootWithException(String command) throws RuntimeException {
        try {
            String output = "";
            String line;
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
                output = output + line + '\n';
            }
            /* remove the last \n */
            if (output.length() > 0) output = output.substring(0,output.length()-1);

            br.close();
            // Lint says while does not loop here (probably because it doesn't do anything except shell error)
            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e("Shell Error:", line);
                throw new RuntimeException();
            }
            br.close();

            process.waitFor();
            process.destroy();
            return output;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String RunAsRootOutput(String command) {
        String output = "";
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
                output = output + line + '\n';
            }
            /* remove the last \n */
            if (output.length() > 0) output = output.substring(0,output.length()-1);
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
        return output;
    }

    public int RunAsRootOutput(String command, final TextView viewLogger) {
        int resultCode = 0;
        String line;
        try {
            //viewLogger.post(() -> viewLogger.append("\n\n ------------ \n\n\n"));
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
                final Spannable tempText = new SpannableString(line + "\n");
                final Spannable timestamp = new SpannableString("[ " + timeStamp.format(new Date()) + " ]  ");
                timestamp.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD561")),0,timestamp.length(),0);
                tempText.setSpan(new ForegroundColorSpan(line.startsWith("[!]")?Color.CYAN:line.startsWith("[+]")?Color.GREEN:line.startsWith("[-]")?Color.parseColor("#D81B60"):Color.WHITE),0,tempText.length(),0);
                viewLogger.post(() -> {
                    viewLogger.append(timestamp);
                    viewLogger.append(tempText);
                });
            }
            //viewLogger.post(() -> viewLogger.append("\n\n ------------ \n\n"));
            br.close();
            br = new BufferedReader(new InputStreamReader(stderr));
            while ((line = br.readLine()) != null) {
                Log.e(TAG, line);
            }
            br.close();
            process.waitFor();
            process.destroy();
            resultCode = process.exitValue();
        } catch (IOException e) {
            Log.d(TAG, "An IOException was caught: " + e.getMessage());
        } catch (InterruptedException ex) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.getMessage());
        }
        return resultCode;
    }

    public int RunAsRootReturnValue(String command) {
        int resultCode = 0;
        try {
            Process process = Runtime.getRuntime().exec("su -mm");
            OutputStream stdin = process.getOutputStream();
            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();
            process.waitFor();
            process.destroy();
            resultCode = process.exitValue();
        } catch (IOException e) {
            Log.d(TAG, "An IOException was caught: " + e.getMessage());
        } catch (InterruptedException ex) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.getMessage());
        }
        return resultCode;
    }

    public String RunAsChrootOutput(String command) {
        String output = "";
        String line;
        try {
            Process process = Runtime.getRuntime().exec("su -mm");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();
            stdin.write((NhPaths.BUSYBOX + " chroot " + NhPaths.CHROOT_PATH() + " " + NhPaths.CHROOT_SUDO + " -E PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH su" + '\n').getBytes());
            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while ((line = br.readLine()) != null) {
                output = output + line + '\n';
            }
            /* remove the last \n */
            if (output.length() > 0) output = output.substring(0,output.length()-1);
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
        return output;
    }

    public int RunAsChrootReturnValue(String command) {
        int resultCode = 0;
        try {
            Process process = Runtime.getRuntime().exec("su -mm");
            OutputStream stdin = process.getOutputStream();
            stdin.write((NhPaths.BUSYBOX + " chroot " + NhPaths.CHROOT_PATH() + " " + NhPaths.CHROOT_SUDO + " -E PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH su" + '\n').getBytes());
            stdin.write((command + '\n').getBytes());
            stdin.write(("exit\n").getBytes());
            stdin.flush();
            stdin.close();
            process.waitFor();
            process.destroy();
            resultCode = process.exitValue();
        } catch (IOException e) {
            Log.d(TAG, "An IOException was caught: " + e.getMessage());
        } catch (InterruptedException ex) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.getMessage());
        }
        return resultCode;
    }

    // this method accepts a text viu (prefect for cases like mana fragment)
    // if you need to manipulate the outpput use the SYNC method. (down)
    public void ReadFile_ASYNC(String _path, final EditText v) {
        final String command = "cat " + _path;
        new Thread(() -> {
            String output = "";
            try {
                Process  p = Runtime.getRuntime().exec("su -mm -c " + command);
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output = output +  line + "\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String _output = output;
            v.post(() -> v.setText(_output));
        }).start();
    }
    // WRAP THIS IN THE BACKGROUND IF POSIBLE WHE USING IT
    public String ReadFile_SYNC(String _path) {
        StringBuilder output = new StringBuilder();
        String command = "cat " + _path;
        Process p;
        try {
            p = Runtime.getRuntime().exec("su -mm -c " + command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
    // SAVE FILE CONTENTS: (contents, fullFilePath)
    public boolean SaveFileContents(String contents, String _path){

        String _newCmd = "cat << 'EOF' > "+_path+"\n"+contents+"\nEOF";
        String _res = RunAsRootOutput(_newCmd);
        if(_res.equals("")){ // no error we fine
            return true;
        } else {
            Log.d("ErrorSavingFile: ", "Error: " + _res);
            return false;
        }
    }
}
