package com.example.playlistmaker.media.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    fun createPlaylist(name: String, description: String, coverPath: String) {
        viewModelScope.launch {
            val playlist = Playlist(
                id = 0,
                name = name,
                description = description,
                coverPath = coverPath,
                trackIds = emptyList(),
                trackCount = 0
            )
            playlistInteractor.createPlaylist(playlist)
        }
    }
}