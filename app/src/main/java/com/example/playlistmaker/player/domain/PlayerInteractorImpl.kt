package com.example.playlistmaker.player.domain

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Runnable

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    companion object {
        private const val UPDATE_DEBOUNCE_DELAY = 300L
    }

    private val handler = Handler(Looper.getMainLooper())

    override var state = PlayerInteractor.Companion.STATE_DEFAULT
        private set

    override var currentPosition = 0
        private set

    override var onStateChanged: ((Int) -> Unit)? = null
    override var onPositionChanged: ((Int) -> Unit)? = null

    private val positionRunnable = object : Runnable {
        override fun run() {
            if (state == PlayerInteractor.Companion.STATE_PLAYING) {
                currentPosition = repository.getCurrentPosition()
                onPositionChanged?.invoke(currentPosition)
                handler.postDelayed(this, UPDATE_DEBOUNCE_DELAY)
            }
        }
    }

    init {
        repository.setOnPreparedListener {
            state = PlayerInteractor.Companion.STATE_PREPARED
            currentPosition = 0
            onStateChanged?.invoke(state)
            onPositionChanged?.invoke(0)
        }

        repository.setOnCompletionListener {
            state = PlayerInteractor.Companion.STATE_COMPLETED
            currentPosition = 0
            stopTimer()
            onStateChanged?.invoke(state)
            onPositionChanged?.invoke(0)
        }

        repository.setOnErrorListener {
            state = PlayerInteractor.Companion.STATE_DEFAULT
            onStateChanged?.invoke(state)
        }
    }

    override fun prepare(url: String?) {
        state = PlayerInteractor.Companion.STATE_DEFAULT
        repository.prepare(url)
    }

    override fun start() {
        when (state) {
            PlayerInteractor.Companion.STATE_PREPARED, PlayerInteractor.Companion.STATE_PAUSED -> {
                repository.start()
                state = PlayerInteractor.Companion.STATE_PLAYING
                startTimer()
                onStateChanged?.invoke(state)
            }
        }
    }

    override fun pause() {
        when (state) {
            PlayerInteractor.Companion.STATE_PLAYING -> {
                repository.pause()
                state = PlayerInteractor.Companion.STATE_PAUSED
                stopTimer()
                onStateChanged?.invoke(state)
            }
        }
    }

    override fun release() {
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        repository.release()
    }

    private fun startTimer() {
        handler.removeCallbacks(positionRunnable)
        handler.post(positionRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(positionRunnable)
    }
}