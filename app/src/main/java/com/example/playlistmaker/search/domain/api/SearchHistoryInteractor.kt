package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Resource
import com.example.playlistmaker.search.data.dto.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun clearHistory()
    fun addToHistory(track: Track)

    interface HistoryConsumer {
        fun consume(history: Resource<List<Track>>)
    }
}