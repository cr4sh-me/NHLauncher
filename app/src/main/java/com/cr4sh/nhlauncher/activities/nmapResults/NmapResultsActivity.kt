package com.cr4sh.nhlauncher.activities.nmapResults

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.ToastUtils
import com.flask.colorpicker.BuildConfig
import kotlinx.coroutines.launch
import java.io.File


class NmapResultsActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nmap_webview_layout)

        // Get the file path of the scan results from the intent
        val filePath = intent.getStringExtra("file_path")

        // Load the scan results into the WebView
        val webView = findViewById<WebView>(R.id.webview)
        val webSettings = webView.settings
        val openInBrowserButton = findViewById<Button>(R.id.open_in_browser)
        val moveBackButton = findViewById<Button>(R.id.cancel_button)

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


}