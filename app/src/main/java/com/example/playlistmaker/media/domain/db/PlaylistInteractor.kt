package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Int)
}