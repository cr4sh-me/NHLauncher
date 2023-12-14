package com.cr4sh.nhlauncher;

import android.content.pm.PackageInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateChecker {

    private static final String TAG = "UpdateChecker";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/cr4sh-me/NHLauncher/releases/latest";
    MainActivity mainActivity;

    public UpdateChecker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public UpdateCheckResult checkUpdate() {
        try {
            String latestVersion = getLatestAppVersion();
            return compareVersions(latestVersion);
        } catch (IOException e) {
            Log.e(TAG, "Error checking for updates", e);
            return new UpdateCheckResult(false, e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getLatestAppVersion() throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GITHUB_API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                return json.getString("tag_name");
            } else {
                int responseCode = response.code(); // Get the HTTP response code
                Log.e(TAG, "Failed to retrieve the latest app version. HTTP Code: " + responseCode);
                throw new IOException("Failed to retrieve the latest app version. HTTP Code: " + responseCode);
            }
        } catch (UnknownHostException | SocketTimeoutException e) {
            // Handle the case where the host cannot be resolved or there is a socket timeout
            Log.e(TAG, "Error checking for updates", e);
            throw new IOException(mainActivity.getResources().getString(R.string.check_internet_connection));
        }
    }


    private UpdateCheckResult compareVersions(String latestVersion) {
        String installedVersion = getInstalledVersion();

        if (installedVersion == null) {
            return new UpdateCheckResult(false, mainActivity.getResources().getString(R.string.something_fucked_up));
        }

        Log.d(TAG, "cURRENT: " + installedVersion);


        String[] installedParts = installedVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < installedParts.length && i < latestParts.length; i++) {
            int installedNumber = Integer.parseInt(installedParts[i]);

            int latestNumber = Integer.parseInt(latestParts[i]);

            Log.d(TAG, "after: " + installedVersion);

            if (installedNumber < latestNumber) {
                Log.d(TAG, latestVersion + " < " + installedVersion);
                return new UpdateCheckResult(true, mainActivity.getResources().getString(R.string.update_avaiable) +
                        "\n" + mainActivity.getResources().getString(R.string.current_app_version) + installedVersion +
                        "\n" + mainActivity.getResources().getString(R.string.new_app_version) + latestVersion);
            } else if (installedNumber > latestNumber) {
                Log.d(TAG, latestVersion + " > " + installedVersion);
                return new UpdateCheckResult(false, mainActivity.getResources().getString(R.string.giga_chad));
            }
            // If equal, check the next part
        }

        // If the loop completes, consider versions equal
        Log.d(TAG, latestVersion + " == " + installedVersion);
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

    public static class UpdateCheckResult {
        private final boolean isUpdateAvailable;
        private final String message;

        public UpdateCheckResult(boolean isUpdateAvailable, String message) {
            this.isUpdateAvailable = isUpdateAvailable;
            this.message = message;
        }

        public boolean isUpdateAvailable() {
            return isUpdateAvailable;
        }

        public String getMessage() {
            return message;
        }
    }
}
