package com.cr4sh.nhlanucher.bridge;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class Runner extends AppCompatActivity{
  public static AppCompatActivity activity;
  public static WeakReference<Context> context = null;

  // Meant to be used in context
  public static void run_cmd(String cmd) {
    Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
    context.get().startActivity(intent);
  }

  public static void run_cmd_android(String cmd) {
    Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/android-su", cmd);
    context.get().startActivity(intent);
  }

  // Meant to be used in activity
  public static void run_cmd_activity(String cmd) {
    Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/kali", cmd);
    activity.startActivity(intent);
  }

  public static void run_cmd_android_aactivity(String cmd) {
    Intent intent = Bridge.createExecuteIntent("/data/data/com.offsec.nhterm/files/usr/bin/android-su", cmd);
    activity.startActivity(intent);
  }
}
