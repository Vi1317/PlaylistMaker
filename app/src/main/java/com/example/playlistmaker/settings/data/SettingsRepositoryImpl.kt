package com.example.playlistmaker.settings.data

import com.example.playlistmaker.search.data.dto.StorageClient
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl (private val storage: StorageClient<ThemeSettings>) :
    SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return storage.getData() ?: ThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storage.storeData(settings)
    }
}