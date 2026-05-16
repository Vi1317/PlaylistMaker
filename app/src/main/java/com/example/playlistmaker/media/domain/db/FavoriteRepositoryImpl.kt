package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.FavoriteDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val favoriteDbConvertor: FavoriteDbConvertor,
) : FavoriteRepository {

    override suspend fun addToFavorite(track: Track) {
        val entity = favoriteDbConvertor.map(track)
        appDatabase.favoriteDao().insertFavorite(entity)
    }

    override suspend fun deleteFromFavorite(track: Track) {
        val entity = favoriteDbConvertor.map(track)
        appDatabase.favoriteDao().deleteFavorite(entity)
    }

    override fun getFavorite(): Flow<List<Track>> {
        return appDatabase.favoriteDao().getFavorite().map { entities ->
            entities.map { entity ->
                favoriteDbConvertor.map(entity).apply { isFavorite = true }
            }
        }
    }

    override suspend fun getFavoriteIds(): List<Int> {
        return appDatabase.favoriteDao().getFavoriteIds()
    }
}