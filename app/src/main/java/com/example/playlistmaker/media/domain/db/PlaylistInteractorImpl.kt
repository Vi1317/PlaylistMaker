package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor{
    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        repository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        return repository.getTracksByIds(trackIds)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Int) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }
}