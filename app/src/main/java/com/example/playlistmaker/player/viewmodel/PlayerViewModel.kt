package com.example.playlistmaker.player.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    companion object {
        private const val UPDATE_DELAY = 300L
    }

    private var timerJob: Job? = null
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
        timerJob?.cancel()
        _state.postValue(_state.value?.copy(isPlaying = false))
    }
    private fun updateTime() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isPlaying) {
                val time = formatTime(playerInteractor.getCurrentPosition())
                _state.postValue(_state.value?.copy(currentTime = time))
                delay(UPDATE_DELAY)
            }
        }
    }


    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        playerInteractor.release()
    }
}
data class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val currentTime: String
)