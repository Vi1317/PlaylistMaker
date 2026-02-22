package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun get(): List<Track>
    fun clear()
    fun add(track: Track)
}