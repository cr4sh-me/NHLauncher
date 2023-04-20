package com.cr4sh.nhlanucher;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.cr4sh.nhlanucher.dialogs.AppsDialog;
import com.cr4sh.nhlanucher.dialogs.CustomThemeDialog;
import com.cr4sh.nhlanucher.dialogs.DeleteToolDialog;
import com.cr4sh.nhlanucher.dialogs.EditableDialog;
import com.cr4sh.nhlanucher.dialogs.FirstSetupDialog;
import com.cr4sh.nhlanucher.dialogs.NewToolDialog;
import com.cr4sh.nhlanucher.dialogs.PermissionDialog;
import com.cr4sh.nhlanucher.dialogs.SettingsDialog;
import com.cr4sh.nhlanucher.dialogs.StatisticsDialog;

public class DialogUtils {
    private final FragmentManager fragmentManager;

    public DialogUtils(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void openEditableDialog(String name, String cmd) {
        EditableDialog editableDialog = new EditableDialog();
        // Input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("cmd", cmd);
        editableDialog.setArguments(args);
        // Display our dialog!
        editableDialog.show(fragmentManager, "EditableDialog");
    }

    public void openNewToolDialog(String category) {
        NewToolDialog ntDialog = new NewToolDialog();
        // ARGS!
        Bundle args = new Bundle();
        args.putString("category", category);
        ntDialog.setArguments(args);
        // Display our dialog!
        ntDialog.show(fragmentManager, "NewToolDialog");
    }

    public void openDeleteToolDialog(String name) {
        DeleteToolDialog dtDialog = new DeleteToolDialog();
        // ARGS!
        Bundle args = new Bundle();
        args.putString("name", name);
        dtDialog.setArguments(args);
        // Display our dialog!
        dtDialog.show(fragmentManager, "DeleteToolDialog");
    }

    public void openAppsDialog() {
        AppsDialog apDialog = new AppsDialog();
        // Display our dialog!
        apDialog.show(fragmentManager, "AppsDialog");
    }

    public void openFirstSetupDialog() {
        FirstSetupDialog fsDialog = new FirstSetupDialog();
        // Display our dialog!
        fsDialog.show(fragmentManager, "FirstSetupDialog");
    }

    public void openCustomThemesDialog() {
        new Thread(() -> {
            CustomThemeDialog ctDialog = new CustomThemeDialog();
            // Display our dialog!
            ctDialog.show(fragmentManager, "CustomThemeDialog");
        }).start();
    }

    public void openSettingsDialog() {
        SettingsDialog stDialog = new SettingsDialog();
        // Display our dialog!
        stDialog.show(fragmentManager, "SettingsDialog");
    }

    public void openPermissionsDialog() {
        PermissionDialog pmDialog = new PermissionDialog();
        // Display our dialog!
        pmDialog.show(fragmentManager, "PermissionsDialog");
    }

    public void openStatisticsDialog() {
        StatisticsDialog statsDialog = new StatisticsDialog();
        // Display our dialog!
        statsDialog.show(fragmentManager, "PermissionsDialog");
    }
}