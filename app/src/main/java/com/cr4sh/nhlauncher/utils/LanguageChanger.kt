package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


open class LanguageChanger : AppCompatActivity() {

    private lateinit var nhlPreferences: NHLPreferences
    override fun attachBaseContext(newBase: Context) {
        nhlPreferences = NHLPreferences(newBase)
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }

    private fun updateBaseContextLocale(context: Context): Context? {

        val x = Locale(nhlPreferences.languageLocale().lowercase())

        // Use older method for api <= 25
        return if (android.os.Build.VERSION.SDK_INT <= 25) {
            updateResources(context, x)
        } else {
            updateResourcesLegacy(context, x)
        }
    }

    private fun updateResources(context: Context, locale: Locale): Context? {
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}