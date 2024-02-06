package com.cr4sh.nhlauncher.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


class LanguageChanger : AppCompatActivity() {

    fun setLocale(context: Context?, localeSpec: String): Context? {
//            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(localeSpec)
//            AppCompatDelegate.setApplicationLocales(appLocale)
//            context?.createConfigurationContext(context.resources.configuration)
        val locale = Locale(localeSpec)
        Locale.setDefault(locale)
        return updateResourcesLegacy(context!!, locale)
    }

    private fun updateResources(context: Context, locale: Locale): Context? {
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, locale: Locale): Context? {
        val resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}