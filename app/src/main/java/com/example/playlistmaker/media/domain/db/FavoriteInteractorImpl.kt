package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(
    private val repository: FavoriteRepository
)  : FavoriteInteractor{
    override suspend fun addToFavorite(track: Track) {
        repository.addToFavorite(track)
    }

    override suspend fun deleteFromFavorite(track: Track) {
        repository.deleteFromFavorite(track)
    }

    override fun getFavorite(): Flow<List<Track>> {
        return repository.getFavorite()
    }

    override suspend fun getFavoriteIds(): List<Int> {
        return repository.getFavoriteIds()
    }
}