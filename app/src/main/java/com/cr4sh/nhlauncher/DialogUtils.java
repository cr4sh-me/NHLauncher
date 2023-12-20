package com.cr4sh.nhlauncher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.FragmentManager;

import com.cr4sh.nhlauncher.dialogs.AppsDialog;
import com.cr4sh.nhlauncher.dialogs.ButtonMenuDialog;
import com.cr4sh.nhlauncher.dialogs.CustomThemeDialog;
import com.cr4sh.nhlauncher.dialogs.DeleteToolDialog;
import com.cr4sh.nhlauncher.dialogs.EditableDialog;
import com.cr4sh.nhlauncher.dialogs.FirstSetupDialog;
import com.cr4sh.nhlauncher.dialogs.MissingActivityDialog;
import com.cr4sh.nhlauncher.dialogs.NewToolDialog;
import com.cr4sh.nhlauncher.dialogs.NhlColorPickerDialog;
import com.cr4sh.nhlauncher.dialogs.PermissionDialog;
import com.cr4sh.nhlauncher.dialogs.SettingsDialog;
import com.cr4sh.nhlauncher.dialogs.StatisticsDialog;
import com.cr4sh.nhlauncher.dialogs.ThrottlingDialog;
import com.cr4sh.nhlauncher.dialogs.ToolbarDialog;
import com.cr4sh.nhlauncher.dialogs.WpsCustomPinDialog;

// This class creates and opens Dialogs
public class DialogUtils {

    private final FragmentManager fragmentManager;

//    MainActivity myActivity;

//    public DialogUtils(FragmentManager fragmentManager, MainActivity activity) {
//        this.fragmentManager = fragmentManager;
//        this.myActivity = activity;
//    }

    public DialogUtils(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void openEditableDialog(String name, String cmd) {
        new Thread(() -> {
            EditableDialog editableDialog = new EditableDialog();
            // Input as an argument.
            Bundle args = new Bundle();
            args.putString("name", name);
            args.putString("cmd", cmd);
            editableDialog.setArguments(args);
            // Display our dialog!
            editableDialog.show(fragmentManager, "EditableDialog");
        }).start();
    }


    public void openNewToolDialog(String category) {
        new Thread(() -> {
            NewToolDialog ntDialog = new NewToolDialog();
            // ARGS!
            Bundle args = new Bundle();
            args.putString("category", category);
            ntDialog.setArguments(args);
            // Display our dialog!
            ntDialog.show(fragmentManager, "NewToolDialog");
        }).start();
    }

    public void openDeleteToolDialog(String name) {
        new Thread(() -> {
            DeleteToolDialog dtDialog = new DeleteToolDialog();
            // ARGS!
            Bundle args = new Bundle();
            args.putString("name", name);
            dtDialog.setArguments(args);
            // Display our dialog!
            dtDialog.show(fragmentManager, "DeleteToolDialog");
        }).start();
    }

    public void openAppsDialog() {
        new Thread(() -> {
            AppsDialog apDialog = new AppsDialog();
            // Display our dialog!
            apDialog.show(fragmentManager, "AppsDialog");
        }).start();
    }

    public void openFirstSetupDialog() {
        new Thread(() -> {
            FirstSetupDialog fsDialog = new FirstSetupDialog();
            // Display our dialog!
            fsDialog.show(fragmentManager, "FirstSetupDialog");
        }).start();
    }

    public void openSettingsDialog() {
        new Thread(() -> {
            SettingsDialog stDialog = new SettingsDialog();
            // Display our dialog!
            stDialog.show(fragmentManager, "SettingsDialog");
        }).start();
    }

    public void openPermissionsDialog() {
        new Thread(() -> {
            PermissionDialog pmDialog = new PermissionDialog();
            // Display our dialog!
            pmDialog.show(fragmentManager, "PermissionsDialog");
        }).start();
    }

    public void openStatisticsDialog(MainActivity myActivity) {
        new Thread(() -> {
            StatisticsDialog statsDialog = new StatisticsDialog(myActivity);
            // Display our dialog!
            statsDialog.show(fragmentManager, "PermissionsDialog");
        }).start();
    }

    public void openMissingActivityDialog() {
        new Thread(() -> {
            MissingActivityDialog maDialog = new MissingActivityDialog();
            // Display our dialog!
            maDialog.show(fragmentManager, "MissingActivityDialog");
        }).start();
    }

    public void openButtonMenuDialog(MainActivity myActivity) {
        new Thread(() -> {
            ButtonMenuDialog bmDialog = new ButtonMenuDialog(myActivity);
            // ARGS!
//            Bundle args = new Bundle();
//            args.putString("category", category);
//            bmDialog.setArguments(args);
            // Display our dialog!
            bmDialog.show(fragmentManager, "ButtonMenuDialog");
        }).start();
    }

    public void openCustomThemesDialog() {
        new Thread(() -> {
            CustomThemeDialog ctDialog = new CustomThemeDialog();
            // Display our dialog!
            ctDialog.show(fragmentManager, "CustomThemeDialog");
        }).start();
    }

    public void openToolbarDialog(MainActivity myActivity) {
        new Thread(() -> {
            ToolbarDialog tbDialog = new ToolbarDialog(myActivity);
            // Display our dialog!
            tbDialog.show(fragmentManager, "ToolbarDialog");
        }).start();
    }

    public void openNhlColorPickerDialog(Button button, ImageView alpha, String colorShade) {
        new Thread(() -> {
            NhlColorPickerDialog nhlcpDialog = new NhlColorPickerDialog(button, alpha, colorShade);
            // Display our dialog!
            nhlcpDialog.show(fragmentManager, "NhlColorPickerDialog");
        }).start();
    }

    public void openThrottlingDialog() {
        new Thread(() -> {
            ThrottlingDialog thDialog = new ThrottlingDialog();
            // Display our dialog!
            thDialog.show(fragmentManager, "PermissionsDialog");
        }).start();
    }

    public void openWpsCustomSetting(int number, WPSAttack activity) {
        new Thread(() -> {
            WpsCustomPinDialog pinDialog = new WpsCustomPinDialog(activity);
            Bundle args = new Bundle();
            args.putInt("option", number);
            pinDialog.setArguments(args);
            // Display our dialog!
            pinDialog.show(fragmentManager, "PermissionsDialog");
        }).start();
    }
}