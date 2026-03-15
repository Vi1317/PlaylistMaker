package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {
    private val type = object : TypeToken<ArrayList<TrackEntity>>() {}.type

    fun storeData(tracks: List<TrackEntity>) {
        sharedPrefs.edit()
            .putString(KEY_HISTORY, gson.toJson(tracks, type))
            .apply()
    }

    fun getData(): List<TrackEntity> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return gson.fromJson(json, type)
    }

    fun clearData() {
        sharedPrefs.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val KEY_HISTORY = "search_history"
    }
}