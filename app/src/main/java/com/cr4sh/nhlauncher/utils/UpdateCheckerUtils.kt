package com.cr4sh.nhlauncher.utils;

import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateCheckerUtils {

    private static final String TAG = "UpdateCheckerUtils";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/cr4sh-me/NHLauncher/releases/latest";
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private final ExecutorService executor = NHLManager.getInstance().getExecutorService();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public UpdateCheckerUtils() {
    }

    public void checkUpdateAsync(UpdateCheckListener listener) {
        executor.submit(() -> {
            try {
                Log.d(TAG, "Checking for updates asynchronously...");
                String latestVersion = getLatestAppVersion();
                Log.d(TAG, "Checking for updates asynchronously2...");
                UpdateCheckResult updateResult = compareVersions(latestVersion);
                Log.d(TAG, "Update check result: " + updateResult);
                postUpdateCheckResult(listener, updateResult);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error checking for updates", e);
                Log.d(TAG, "Update check failed: " + e.getMessage());
                postUpdateCheckResult(listener, new UpdateCheckResult(false, e.getMessage()));
            }
        });
    }

    private void postUpdateCheckResult(UpdateCheckListener listener, UpdateCheckResult updateResult) {
        uiHandler.post(() -> {
            Log.d(TAG, "Posting update check result to the UI thread");
            listener.onUpdateCheckCompleted(updateResult);
        });
    }

    private String getLatestAppVersion() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GITHUB_API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                String latestVersion = json.getString("tag_name");
                Log.v(TAG, "Latest version: " + latestVersion);
                return latestVersion;
            } else {
                handleHttpErrors(response.code());
                return null;
            }
        } catch (UnknownHostException | SocketTimeoutException e) {
            Log.e(TAG, "Error checking for updates", e);
            throw new IOException(mainActivity.getResources().getString(R.string.check_internet_connection));
        }
    }

    private void handleHttpErrors(int responseCode) throws IOException {
        if (responseCode == 403) {
            Log.e(TAG, "Too many requests! Error 403");
            throw new IOException("API limit exceeded. Try again later!");
        } else {
            Log.e(TAG, "Failed to retrieve app version. HTTP Code: " + responseCode);
            throw new IOException("Failed to retrieve app version. HTTP Code: " + responseCode);
        }
    }

    private UpdateCheckResult compareVersions(String latestVersion) {
        String installedVersion = getInstalledVersion();

        if (installedVersion == null) {
            return new UpdateCheckResult(false, mainActivity.getResources().getString(R.string.something_fucked_up));
        }

        Log.d(TAG, "Current version: " + installedVersion);

        String[] installedParts = installedVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < installedParts.length || i < latestParts.length; i++) {
            String installedComponent = (i < installedParts.length) ? installedParts[i] : "0";
            String latestComponent = (i < latestParts.length) ? latestParts[i] : "0";

            Log.d(TAG, "Checking version component: " + installedComponent + " vs " + latestComponent);

            int comparisonResult = installedComponent.compareTo(latestComponent);

            if (comparisonResult < 0) {
                Log.d(TAG, "Update available: " + latestVersion + " > " + installedVersion);
                return new UpdateCheckResult(true, mainActivity.getResources().getString(R.string.update_avaiable) +
                        "\n" + mainActivity.getResources().getString(R.string.current_app_version) + installedVersion +
                        "\n" + mainActivity.getResources().getString(R.string.new_app_version) + latestVersion);
            } else if (comparisonResult > 0) {
                Log.d(TAG, "giga chad: " + latestVersion + " <= " + installedVersion);
                return new UpdateCheckResult(false, mainActivity.getResources().getString(R.string.giga_chad));
            }
        }

        // If loop completes, versions are equal
        Log.d(TAG, "No update needed: " + latestVersion + " == " + installedVersion);
        return new UpdateCheckResult(false, mainActivity.getResources().getString(R.string.already_updated));
    }

    private String getInstalledVersion() {
        try {
            PackageInfo packageInfo = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, "Error getting installed version", e);
            return null;
        }
    }


    public interface UpdateCheckListener {
        void onUpdateCheckCompleted(UpdateCheckResult updateResult);
    }

    public record UpdateCheckResult(boolean isUpdateAvailable, String message) {
    }
}
