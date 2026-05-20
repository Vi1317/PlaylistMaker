package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.Int

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

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val trackEntity = TrackInPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        appDatabase.trackInPlaylistDao().insertTrack(trackEntity)

        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = playlist.trackCount + 1
        )

        val entity = playlistDbConvertor.map(updatedPlaylist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }
}