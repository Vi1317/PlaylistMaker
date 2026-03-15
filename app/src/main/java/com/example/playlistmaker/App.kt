package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import org.koin.core.context.startKoin
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.android.inject
import com.example.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, viewModelModule)
        }

        val settingsInteractor: SettingsInteractor by inject()
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