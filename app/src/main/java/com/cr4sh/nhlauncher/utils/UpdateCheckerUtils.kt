package com.cr4sh.nhlauncher.utils

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UpdateCheckerUtils {
    private val mainActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    fun checkUpdateAsync(listener: UpdateCheckListener) {
        mainActivity?.lifecycleScope?.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Checking for updates asynchronously...")
                val latestVersion = latestAppVersion
                Log.d(TAG, "Checking for updates asynchronously2...")
                val updateResult = compareVersions(latestVersion)
                Log.d(TAG, "Update check result: $updateResult")
                withContext(Dispatchers.Main) {
                    postUpdateCheckResult(listener, updateResult)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error checking for updates", e)
                Log.d(TAG, "Update check failed: " + e.message)
                withContext(Dispatchers.Main) {
                    postUpdateCheckResult(listener, UpdateCheckResult(false, e.message))
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Error checking for updates", e)
                Log.d(TAG, "Update check failed: " + e.message)
                withContext(Dispatchers.Main) {
                    postUpdateCheckResult(listener, UpdateCheckResult(false, e.message))
                }
            }
        }
    }

    private fun postUpdateCheckResult(
        listener: UpdateCheckListener,
        updateResult: UpdateCheckResult
    ) {
        mainActivity?.lifecycleScope?.launch {
            Log.d(TAG, "Posting update check result to the UI thread")
            listener.onUpdateCheckCompleted(updateResult)
        }
    }

    @get:Throws(IOException::class, JSONException::class)
    private val latestAppVersion: String?
        get() {
            val client = OkHttpClient()
            val request: Request = Request.Builder().url(GITHUB_API_URL).build()
            try {
                client.newCall(request).execute().use { response ->
                    return if (response.isSuccessful) {
                        assert(response.body != null)
                        val responseBody = response.body!!.string()
                        val json = JSONObject(responseBody)
                        val latestVersion = json.getString("tag_name")
                        Log.v(TAG, "Latest version: $latestVersion")
                        latestVersion
                    } else {
                        handleHttpErrors(response.code)
                        null
                    }
                }
            } catch (e: UnknownHostException) {
                Log.e(TAG, "Error checking for updates", e)
                throw IOException(mainActivity?.resources?.getString(R.string.check_internet_connection))
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Error checking for updates", e)
                throw IOException(mainActivity?.resources?.getString(R.string.check_internet_connection))
            }
        }

    @Throws(IOException::class)
    private fun handleHttpErrors(responseCode: Int) {
        if (responseCode == 403) {
            Log.e(TAG, "Too many requests! Error 403")
            throw IOException("API limit exceeded. Try again later!")
        } else {
            Log.e(TAG, "Failed to retrieve app version. HTTP Code: $responseCode")
            throw IOException("Failed to retrieve app version. HTTP Code: $responseCode")
        }
    }

    private fun compareVersions(latestVersion: String?): UpdateCheckResult {
        val installedVersion = installedVersion
            ?: return UpdateCheckResult(
                false,
                mainActivity?.resources?.getString(R.string.something_fucked_up)
            )
        Log.d(TAG, "Current version: $installedVersion")
        val installedParts =
            installedVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val latestParts =
            latestVersion!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i = 0
        while (i < installedParts.size || i < latestParts.size) {
            val installedComponent = if (i < installedParts.size) installedParts[i] else "0"
            val latestComponent = if (i < latestParts.size) latestParts[i] else "0"
            Log.d(TAG, "Checking version component: $installedComponent vs $latestComponent")
            val comparisonResult = installedComponent.compareTo(latestComponent)
            if (comparisonResult < 0) {
                Log.d(TAG, "Update available: $latestVersion > $installedVersion")
                return UpdateCheckResult(
                    true, """
     ${mainActivity?.resources?.getString(R.string.update_avaiable)}
     ${mainActivity?.resources?.getString(R.string.current_app_version)}$installedVersion
     ${mainActivity?.resources?.getString(R.string.new_app_version)}$latestVersion
     """.trimIndent()
                )
            } else if (comparisonResult > 0) {
                Log.d(TAG, "giga chad: $latestVersion <= $installedVersion")
                return UpdateCheckResult(
                    false,
                    mainActivity?.resources?.getString(R.string.giga_chad)
                )
            }
            i++
        }

        // If loop completes, versions are equal
        Log.d(TAG, "No update needed: $latestVersion == $installedVersion")
        return UpdateCheckResult(
            false,
            mainActivity?.resources?.getString(R.string.already_updated)
        )
    }

    private val installedVersion: String?
        get() = try {
            val packageInfo =
                mainActivity?.let { mainActivity.packageManager.getPackageInfo(it.packageName, 0) }
            packageInfo?.versionName
        } catch (e: Exception) {
            Log.e(TAG, "Error getting installed version", e)
            null
        }

    interface UpdateCheckListener {
        fun onUpdateCheckCompleted(updateResult: UpdateCheckResult?)
    }

    @JvmRecord
    data class UpdateCheckResult(val isUpdateAvailable: Boolean, val message: String?)
    companion object {
        private const val TAG = "UpdateCheckerUtils"
        private const val GITHUB_API_URL =
            "https://api.github.com/repos/cr4sh-me/NHLauncher/releases/latest"
    }
}
