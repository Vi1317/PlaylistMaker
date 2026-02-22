package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import kotlinx.coroutines.Runnable

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    companion object {
        private const val UPDATE_DEBOUNCE_DELAY = 300L
    }

    private val handler = Handler(Looper.getMainLooper())

    override var state = PlayerInteractor.STATE_DEFAULT
        private set

    override var currentPosition = 0
        private set

    override var onStateChanged: ((Int) -> Unit)? = null
    override var onPositionChanged: ((Int) -> Unit)? = null

    private val positionRunnable = object : Runnable {
        override fun run() {
            if (playerRepository.isPlaying()) {
                currentPosition = playerRepository.getCurrentPosition()
                onPositionChanged?.invoke(currentPosition)
                handler.postDelayed(this, UPDATE_DEBOUNCE_DELAY)
            }
        }
    }

    init {
        playerRepository.setOnPreparedListener {
            state = PlayerInteractor.STATE_PREPARED
            currentPosition = 0
            onStateChanged?.invoke(state)
            onPositionChanged?.invoke(0)
        }

        playerRepository.setOnCompletionListener {
            state = PlayerInteractor.STATE_COMPLETED
            currentPosition = 0
            stopTimer()
            onStateChanged?.invoke(state)
            onPositionChanged?.invoke(0)
        }

        playerRepository.setOnErrorListener {
            state = PlayerInteractor.STATE_DEFAULT
            onStateChanged?.invoke(state)
        }
    }

    override fun prepare(url: String?) {
        state = PlayerInteractor.STATE_DEFAULT
        playerRepository.prepare(url)
    }

    override fun start() {
        when (state) {
            PlayerInteractor.STATE_PREPARED, PlayerInteractor.STATE_PAUSED -> {
                playerRepository.start()
                state = PlayerInteractor.STATE_PLAYING
                startTimer()
                onStateChanged?.invoke(state)
            }
        }
    }

    override fun pause() {
        when (state) {
            PlayerInteractor.STATE_PLAYING -> {
                playerRepository.pause()
                state = PlayerInteractor.STATE_PAUSED
                stopTimer()
                onStateChanged?.invoke(state)
            }
        }
    }

    override fun release() {
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        playerRepository.release()
    }

    private fun startTimer() {
        handler.removeCallbacks(positionRunnable)
        handler.post(positionRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(positionRunnable)
    }
}