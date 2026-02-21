package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.SearchHistoryManager
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl (private val searchHistoryManager: SearchHistoryManager) : SearchHistoryRepository {
        override fun readHistory(): List<Track> {
            return searchHistoryManager.readHistory()
        }

        override fun clearHistory() {
            searchHistoryManager.clearHistory()
        }

        override fun addToHistory(track: Track) {
            searchHistoryManager.addToHistory(track)
        }
}