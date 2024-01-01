package com.cr4sh.nhlauncher;

// Store MainActivity
public class NHLManager {
    private static NHLManager instance;
    private MainActivity mainActivity;

    private NHLManager() {
        // Private constructor to prevent instantiation
    }

    public static NHLManager getInstance() {
        if (instance == null) {
            instance = new NHLManager();
        }
        return instance;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity activity) {
        this.mainActivity = activity;
    }
}
