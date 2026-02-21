package com.example.playlistmaker.data.dto

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(val sharedPreferences: SharedPreferences) {
    private val historyKey = "search_history"
    private val gson = Gson()

    companion object {
        private const val LIST_SIZE = 10
    }

    fun readHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return emptyList<Track>()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addToHistory(track: Track) {
        val json = sharedPreferences.getString(historyKey, null)
        val historyList: MutableList<Track>
        if (json == null) {
            historyList = mutableListOf()
        } else {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            historyList = gson.fromJson(json, type)
        }

        historyList.removeIf { it.trackId == track.trackId }
        historyList.add(0, track)

        sharedPreferences.edit {
            putString(historyKey, gson.toJson(historyList.take(LIST_SIZE)))
            .apply()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit {
            remove(historyKey)
            .apply()
        }
    }

}