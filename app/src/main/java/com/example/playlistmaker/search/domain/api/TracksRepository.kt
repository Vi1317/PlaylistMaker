package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Result<List<Track>>>
}