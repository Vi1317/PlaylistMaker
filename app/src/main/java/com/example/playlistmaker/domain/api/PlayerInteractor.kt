package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val STATE_COMPLETED = 4
    }

    val state: Int
    val currentPosition: Int
    var onStateChanged: ((Int) -> Unit)?
    var onPositionChanged: ((Int) -> Unit)?

    fun prepare(url: String?)
    fun start()
    fun pause()
    fun release()
}