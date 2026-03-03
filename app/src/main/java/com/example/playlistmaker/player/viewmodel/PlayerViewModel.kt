package com.example.playlistmaker.player.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY = 300L

        fun getFactory(playerInteractor: PlayerInteractor, track: Track): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PlayerViewModel(playerInteractor, track) as T
                }
            }
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false

    private val _state = MutableLiveData(
        PlayerState(
            isPlayButtonEnabled = false,
            isPlaying = false,
            currentTime = "00:00"
        )
    )
    val state: LiveData<PlayerState> = _state

    init {
        playerInteractor.setOnPreparedListener {
            _state.postValue(
                PlayerState(
                    isPlayButtonEnabled = true,
                    isPlaying = false,
                    currentTime = "00:00"
                )
            )
        }
        playerInteractor.setOnCompletionListener {
            isPlaying = false
            _state.postValue(
                PlayerState(
                    isPlayButtonEnabled = true,
                    isPlaying = false,
                    currentTime = "00:00"
                )
            )
        }

        playerInteractor.prepare(track.previewUrl)
    }

    fun onPlayButtonClicked() {
        if (isPlaying) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        playerInteractor.start()
        isPlaying = true
        updateTime()
        _state.postValue(_state.value?.copy(isPlaying = true))
    }

    private fun stopTimer() {
        playerInteractor.pause()
        isPlaying = false
        _state.postValue(_state.value?.copy(isPlaying = false))
    }
    private fun updateTime() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying) {
                    val time = formatTime(playerInteractor.getCurrentPosition())
                    _state.postValue(_state.value?.copy(currentTime = time))
                    handler.postDelayed(this, UPDATE_DELAY)
                }
            }
        }, UPDATE_DELAY)
    }


    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
        playerInteractor.release()
    }
}
data class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val currentTime: String
)
