package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            if (result.isSuccess) {
                Pair(result.getOrNull(), null)
            } else {
                Pair(null, result.exceptionOrNull()?.message)
            }
        }
    }
}