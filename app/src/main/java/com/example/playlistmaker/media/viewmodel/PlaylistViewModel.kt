package com.example.playlistmaker.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _state = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = _state

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                _state.value = if (playlists.isEmpty()) {
                    PlaylistState.Empty
                } else {
                    PlaylistState.Content(playlists)
                }
            }
        }
    }

    sealed class PlaylistState {
        object Empty : PlaylistState()
        data class Content (val playlists: List<Playlist>) : PlaylistState()
    }
}