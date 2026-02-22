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
        val type = object : TypeToken<ArrayList<TrackEntity>>() {}.type
        val entities: List<TrackEntity> = gson.fromJson(json, type)
        return entities.map { it.toDomain() }
    }

    fun addToHistory(track: Track) {
        val json = sharedPreferences.getString(historyKey, null)
        val historyList: MutableList<TrackEntity>
        if (json == null) {
            historyList = mutableListOf()
        } else {
            val type = object : TypeToken<ArrayList<TrackEntity>>() {}.type
            historyList = gson.fromJson(json, type)
        }
        val trackEntity = track.toEntity()
        historyList.removeIf { it.trackId == trackEntity.trackId }
        historyList.add(0, trackEntity)

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