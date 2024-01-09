package com.cr4sh.nhlauncher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NHLManager {
    private MainActivity mainActivity;
    private ExecutorService executorService;

    private NHLManager() {
        // Private constructor to prevent instantiation
        executorService = Executors.newSingleThreadExecutor();
    }

    private static class Holder {
        private static final NHLManager INSTANCE = new NHLManager();
    }

    public static NHLManager getInstance() {
        return Holder.INSTANCE;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity activity) {
        this.mainActivity = activity;
    }

    public ExecutorService getExecutorService() {
        if (executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }

    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
