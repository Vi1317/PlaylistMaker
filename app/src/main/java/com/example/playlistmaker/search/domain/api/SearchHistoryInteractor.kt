package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): List<Track>
    fun clearHistory()
    fun addToHistory(track: Track)

    interface HistoryConsumer {
        fun consume(history: Result<List<Track>>)
    }
}