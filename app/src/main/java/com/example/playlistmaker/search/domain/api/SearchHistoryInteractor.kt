package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun clearHistory()
    fun addToHistory(track: Track)

    interface HistoryConsumer {
        fun consume(history: Result<List<Track>>)
    }
}