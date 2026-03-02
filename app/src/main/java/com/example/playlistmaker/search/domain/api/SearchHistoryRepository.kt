package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Resource
import com.example.playlistmaker.search.data.dto.Track

interface SearchHistoryRepository {
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
    fun addToHistory(track: Track)
}