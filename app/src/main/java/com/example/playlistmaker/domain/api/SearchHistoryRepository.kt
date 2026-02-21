package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun readHistory(): List<Track>
    fun clearHistory()
    fun addToHistory(track: Track)
}