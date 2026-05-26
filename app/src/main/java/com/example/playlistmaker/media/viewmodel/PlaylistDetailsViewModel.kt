package com.example.playlistmaker.media.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    application: Application,
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Long
) : AndroidViewModel(application) {
    private val _state = MutableLiveData<PlaylistDetailsState>()
    val state: LiveData<PlaylistDetailsState> = _state

    init {
        loadPlaylistData()
    }

    fun loadPlaylistData() {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val tracks = playlist?.let {
                playlistInteractor.getTracksByIds(it.trackIds)
            } ?: emptyList()

            val totalDuration = tracks.sumOf { it.trackTimeMillis }
            val totalMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(totalDuration)

            _state.value = PlaylistDetailsState(
                playlist = playlist,
                tracks = tracks,
                totalDuration = formatDuration(totalMinutes),
                trackCount = formatTrackCount(tracks.size)
            )
        }
    }

    fun deleteTrackFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            loadPlaylistData()
        }
    }

    suspend fun deletePlaylist() {
        playlistInteractor.deletePlaylist(playlistId)
    }

    private fun formatDuration(minutes: String): String {
        val minutesInt = minutes.toIntOrNull() ?: 0
        return getApplication<Application>().resources.getQuantityString(
            R.plurals.duration_minutes,
            minutesInt,
            minutesInt
        )
    }

    private fun formatTrackCount(count: Int): String {
        return getApplication<Application>().resources.getQuantityString(
            R.plurals.track_count,
            count,
            count
        )
    }
}

data class PlaylistDetailsState(
    val playlist: Playlist?,
    val tracks: List<Track>,
    val totalDuration: String,
    val trackCount: String
)