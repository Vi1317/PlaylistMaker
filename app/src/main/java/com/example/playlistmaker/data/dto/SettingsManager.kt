package com.example.playlistmaker.data.dto

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsManager(val sharedPreferences: SharedPreferences) {
    var darkTheme = sharedPreferences.getBoolean(THEME_KEY, false)
        private set

    companion object {
        private const val THEME_KEY = "dark_theme"
    }

    fun isDarkTheme(): Boolean = darkTheme

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPreferences.edit {
            putBoolean(THEME_KEY, darkThemeEnabled)
        }
        applyTheme()
    }

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}