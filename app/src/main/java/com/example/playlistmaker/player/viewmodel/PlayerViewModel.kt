package com.example.playlistmaker.player.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.services.MusicService
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val track: Track
) : AndroidViewModel(application) {

    private var playerInteractor: PlayerInteractor? = null
    private var musicService: MusicService? = null
    private var serviceObservationJob: Job? = null

    private val _state = MutableLiveData(
        PlayerState(
            isPlayButtonEnabled = false,
            isPlaying = false,
            currentTime = "00:00",
            isFavorite = track.isFavorite,
            isAddedToAnyPlaylist = false,
            playlists = emptyList(),
            addToPlaylistMessage = null
        )
    )
    val state: LiveData<PlayerState> = _state


    init {
        viewModelScope.launch {
            loadPlaylists()
        }
    }

    fun bindService(interactor: PlayerInteractor, service: MusicService) {
        playerInteractor = interactor
        musicService = service

        service.setTrackInfo(track.artistName, track.trackName)

        playerInteractor?.prepare(track.previewUrl)

        playerInteractor?.setOnPreparedListener {
            _state.value = _state.value?.copy(
                isPlayButtonEnabled = true,
                isPlaying = false,
                currentTime = "00:00"
            )
        }
        playerInteractor?.setOnCompletionListener {
            _state.value = _state.value?.copy(
                isPlayButtonEnabled = true,
                isPlaying = false,
                currentTime = "00:00"
            )
        }

        observeServiceState()
    }

    fun unbindService() {
        serviceObservationJob?.cancel()
        playerInteractor = null
        musicService = null
    }

    private fun observeServiceState() {
        serviceObservationJob = viewModelScope.launch {
            musicService?.playerState?.collectLatest { serviceState ->
                _state.value = _state.value?.copy(
                    isPlayButtonEnabled = serviceState.isPrepared,
                    isPlaying = serviceState.isPlaying,
                    currentTime = serviceState.currentTime
                )
            }
        }
    }

    fun onPlayButtonClicked() {
        if (_state.value?.isPlaying == true) {
            playerInteractor?.pause()
        } else {
            playerInteractor?.start()
        }
    }

    fun onAppBackgrounded() {
        if (_state.value?.isPlaying == true) {
            musicService?.showForegroundNotification()
        }
    }

    fun onAppForegrounded() {
        musicService?.hideForegroundNotification()
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentIsFavorite = _state.value?.isFavorite ?: false
            if (currentIsFavorite) {
                favoriteInteractor.deleteFromFavorite(track)
                _state.value = _state.value?.copy(isFavorite = false)
                track.isFavorite = false
            } else {
                favoriteInteractor.addToFavorite(track)
                _state.value = _state.value?.copy(isFavorite = true)
                track.isFavorite = true
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                val isAddedToAnyPlaylist = playlists.any { it.trackIds.contains(track.trackId) }
                _state.value = _state.value?.copy(
                    playlists = playlists,
                    isAddedToAnyPlaylist = isAddedToAnyPlaylist
                )
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.trackIds.contains(track.trackId)) {
                _state.value = _state.value?.copy(
                    addToPlaylistMessage = getApplication<Application>().getString(
                        R.string.track_already_added,
                        playlist.name
                    )
                )
            } else {
                playlistInteractor.addTrackToPlaylist(playlist, track)

                val updatedTrackIds = playlist.trackIds + listOf(track.trackId)
                val updatedPlaylist = playlist.copy(
                    trackIds = updatedTrackIds,
                    trackCount = playlist.trackCount + 1
                )

                val currentPlaylists = _state.value?.playlists ?: emptyList()
                val updatedPlaylists = currentPlaylists.map { p ->
                    if (p.id == playlist.id) updatedPlaylist else p
                }
                _state.value = _state.value?.copy(
                    playlists = updatedPlaylists,
                    isAddedToAnyPlaylist = true,
                    addToPlaylistMessage = getApplication<Application>().getString(
                        R.string.track_added,
                        playlist.name
                    )
                )
            }
            delay(2000)
            _state.value = _state.value?.copy(addToPlaylistMessage = null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}

data class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val currentTime: String,
    val isFavorite: Boolean,
    val isAddedToAnyPlaylist: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val addToPlaylistMessage: String? = null
)