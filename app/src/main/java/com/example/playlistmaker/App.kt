package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractor(this)
        val themeSettings = settingsInteractor.getThemeSettings()

        AppCompatDelegate.setDefaultNightMode(
            if (themeSettings.isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}