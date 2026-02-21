package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val searchHistoryRepository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun get(): List<Track> {
        return searchHistoryRepository.readHistory()
    }

    override fun clear() {
        return searchHistoryRepository.clearHistory()
    }
    override fun add(track: Track) {
        return searchHistoryRepository.addToHistory(track)
    }
}