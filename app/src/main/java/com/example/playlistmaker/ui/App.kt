package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.domain.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractor(this)
        settingsInteractor.applyTheme()
    }
}