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

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return
        val playlist = playlistDbConvertor.map(playlistEntity)

        appDatabase.playlistDao().deletePlaylistById(playlistId)

        playlist.trackIds.forEach { trackId ->
            val trackExistsElsewhere = appDatabase.playlistDao()
                .doesTrackExistInOtherPlaylists(trackId, playlistId)
            if (!trackExistsElsewhere) {
                appDatabase.trackInPlaylistDao().deleteTrack(trackId)
            }
        }
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

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = appDatabase.playlistDao().getPlaylistById(id)
        return entity?.let { playlistDbConvertor.map(it) }
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        if (trackIds.isEmpty()) return emptyList()
        val entities = appDatabase.trackInPlaylistDao().getTracksByIds(trackIds)
        return trackIds.mapNotNull { trackId ->
            entities.find { it.trackId == trackId }?.let { entity ->
                Track(
                    trackId = entity.trackId,
                    trackName = entity.trackName,
                    artistName = entity.artistName,
                    trackTimeMillis = entity.trackTimeMillis,
                    artworkUrl100 = entity.artworkUrl100,
                    collectionName = entity.collectionName,
                    releaseDate = entity.releaseDate,
                    primaryGenreName = entity.primaryGenreName,
                    country = entity.country,
                    previewUrl = entity.previewUrl
                )
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return
        val playlist = playlistDbConvertor.map(playlistEntity)

        val updatedTrackIds = playlist.trackIds.filter { it != trackId }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = playlist.trackCount - 1
        )
        appDatabase.playlistDao().updatePlaylist(playlistDbConvertor.map(updatedPlaylist))

        val trackExistsElsewhere = appDatabase.playlistDao()
            .doesTrackExistInOtherPlaylists(trackId, playlistId)

        if (!trackExistsElsewhere) {
            appDatabase.trackInPlaylistDao().deleteTrack(trackId)
        }
    }
}