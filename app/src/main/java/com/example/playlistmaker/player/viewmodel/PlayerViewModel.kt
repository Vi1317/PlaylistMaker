package com.example.playlistmaker.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.data.dto.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4

        fun getFactory(playerInteractor: PlayerInteractor, track: Track): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PlayerViewModel(playerInteractor, track) as T
                }
            }
        }
    }

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> = _playerState

    private val _progressTime = MutableLiveData("00:00")
    val progressTime: LiveData<String> = _progressTime

    init {
        playerInteractor.onStateChanged = { newState ->
            _playerState.postValue(newState)
        }

        playerInteractor.onPositionChanged = { position ->
            val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
            _progressTime.postValue(time)
        }

        playerInteractor.prepare(track.previewUrl)
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            STATE_PLAYING -> playerInteractor.pause()
            STATE_PREPARED, STATE_PAUSED -> playerInteractor.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }
}