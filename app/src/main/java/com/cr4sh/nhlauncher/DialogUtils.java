package com.cr4sh.nhlauncher;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.cr4sh.nhlauncher.dialogs.AppsDialog;
import com.cr4sh.nhlauncher.dialogs.DeleteToolDialog;
import com.cr4sh.nhlauncher.dialogs.EditableDialog;
import com.cr4sh.nhlauncher.dialogs.FirstSetupDialog;
import com.cr4sh.nhlauncher.dialogs.MissingActivityDialog;
import com.cr4sh.nhlauncher.dialogs.NewToolDialog;
import com.cr4sh.nhlauncher.dialogs.PermissionDialog;
import com.cr4sh.nhlauncher.dialogs.SettingsDialog;
import com.cr4sh.nhlauncher.dialogs.StatisticsDialog;

// This class creates and opens Dialogs
public class DialogUtils {
    private final FragmentManager fragmentManager;

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

    public void openStatisticsDialog() {
        new Thread(() -> {
            StatisticsDialog statsDialog = new StatisticsDialog();
            // Display our dialog!
            statsDialog.show(fragmentManager, "PermissionsDialog");
        }).start();
    }

    public void openMissingActivityDialog(){
        new Thread(() -> {
            MissingActivityDialog maDialog = new MissingActivityDialog();
            // Display our dialog!
            maDialog.show(fragmentManager, "MissingActivityDialog");
        }).start();
    }

}