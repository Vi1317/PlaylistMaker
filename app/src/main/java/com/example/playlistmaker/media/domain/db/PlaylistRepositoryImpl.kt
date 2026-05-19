package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        val entity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(entity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val entity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().deletePlaylist(entity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map {
                playlistDbConvertor.map(it)
            }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlistDbConvertor.map(playlist)
        return appDatabase.playlistDao().updatePlaylist(entity)
    }
}