package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Track

interface TracksRepository {
    fun searchTracks(expression: String): Result<List<Track>>
}