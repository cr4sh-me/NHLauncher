package com.cr4sh.nhlauncher.activities.nmapResults

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.ColorChanger.Companion.activityAnimation
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.ToastUtils
import kotlinx.coroutines.launch
import java.io.File


class NmapResultsActivity : AppCompatActivity() {

    lateinit var nhlPreferences: NHLPreferences

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nmap_webview_layout)

        activityAnimation()

        nhlPreferences = NHLPreferences(this)

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setBackgroundColor(Color.parseColor(nhlPreferences.color20()))
        val window = this.window
        window.statusBarColor = Color.parseColor(nhlPreferences.color20())
        window.navigationBarColor = Color.parseColor(nhlPreferences.color20())
        // Get the file path of the scan results from the intent
        val filePath = intent.getStringExtra("file_path")
        // Load the scan results into the WebView
        val webView = findViewById<WebView>(R.id.webview)
        val webSettings = webView.settings
        val openInBrowserButton = findViewById<Button>(R.id.open_in_browser)
        val moveBackButton = findViewById<Button>(R.id.cancel_button)

        setButtonColors(openInBrowserButton, false)
        setButtonColors(moveBackButton, true)

        webSettings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false

        filePath?.let { File(it).readText() }?.let {
            webView.loadDataWithBaseURL(
                null,
                it,
                "text/html",
                "utf-8",
                null
            )
        }

        openInBrowserButton.setOnClickListener {
            if (filePath != null) {
                openFile(filePath)
            }
        }

        moveBackButton.setOnClickListener {
            finish()
        }
    }

    private fun openFile(filePath: String) {
        val file = File(filePath)

        if (file.exists()) {
            val uri = FileProvider.getUriForFile(this, "com.cr4sh.nhlauncher.fileprovider", file)
            val intent = Intent(ACTION_VIEW)
            intent.setDataAndType(uri, "text/html") // Adjust MIME type based on your file type
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                lifecycleScope.launch {
                    ToastUtils.showCustomToast(this@NmapResultsActivity, "No supported apps")
                }
                e.printStackTrace()
            }
        } else {
            lifecycleScope.launch {
                ToastUtils.showCustomToast(this@NmapResultsActivity, "Something fucked up!")
            }
        }
    }

    private fun setButtonColors(button: Button, cancel: Boolean) {
        button.setBackgroundColor(Color.parseColor(if (cancel) nhlPreferences.color80() else nhlPreferences.color50()))
        button.setTextColor(Color.parseColor(if (cancel) nhlPreferences.color50() else nhlPreferences.color80()))
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            activityAnimation()
        }
    }
}