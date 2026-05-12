package com.example.playlistmaker.media.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class FavoriteViewModel (
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteState>()
    val state: LiveData<FavoriteState> = _state

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorite().collect { tracks ->
                val favoriteTracks = tracks.map { track ->
                    track.apply { isFavorite = true }
                }
                if (favoriteTracks.isEmpty()) {
                    _state.value = FavoriteState.Empty
                } else {
                    _state.value = FavoriteState.Content(tracks)
                }
            }
        }
    }

    sealed class FavoriteState {
        object Empty : FavoriteState()
        data class Content(val tracks: List<Track>) : FavoriteState()
    }
}