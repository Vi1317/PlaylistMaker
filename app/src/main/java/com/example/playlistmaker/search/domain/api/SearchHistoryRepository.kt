package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface SearchHistoryRepository {
    fun getHistory(): Result<List<Track>>
    fun clearHistory()
    fun addToHistory(track: Track)
}