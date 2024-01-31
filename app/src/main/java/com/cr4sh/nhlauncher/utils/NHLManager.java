package com.cr4sh.nhlauncher.utils;

import com.cr4sh.nhlauncher.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NHLManager {
    private MainActivity mainActivity;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;

    private NHLManager() {
        // Private constructor to prevent instantiation
        executorService = Executors.newCachedThreadPool();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
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
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        if (scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduledExecutorService;
    }

    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

    private static class Holder {
        private static final NHLManager INSTANCE = new NHLManager();
    }
}
