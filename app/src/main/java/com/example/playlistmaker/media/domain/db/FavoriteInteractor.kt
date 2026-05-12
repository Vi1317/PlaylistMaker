package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addToFavorite(track: Track)
    suspend fun deleteFromFavorite(track: Track)
    fun getFavorite(): Flow<List<Track>>
    suspend fun getFavoriteIds(): List<Int>
}