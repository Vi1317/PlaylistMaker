package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ThemeStorage(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {
    private val type = object : TypeToken<ThemeSettings>() {}.type

    fun storeData(settings: ThemeSettings) {
        sharedPrefs.edit()
            .putString(KEY_THEME, gson.toJson(settings, type))
            .apply()
    }

    fun getData(): ThemeSettings {
        val json = sharedPrefs.getString(KEY_THEME, null)
        return if (json == null) ThemeSettings() else gson.fromJson(json, type)
    }

    companion object {
        private const val KEY_THEME = "theme_settings"
    }
}