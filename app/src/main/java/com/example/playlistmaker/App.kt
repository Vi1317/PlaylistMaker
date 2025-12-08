package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {

    var darkTheme = false
    private val prefsName = "app_settings"
    private val themeKey = "dark_theme"

    override fun onCreate() {
        super.onCreate()
        loadTheme()
    }

    private fun loadTheme() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        darkTheme = prefs.getBoolean(themeKey, false)
        applyTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        prefs.edit {
            putBoolean(themeKey, darkThemeEnabled)
        }
        applyTheme()
    }

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
