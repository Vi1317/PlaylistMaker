package com.example.playlistmaker.player.domain

interface PlayerInteractor {
    fun prepare(url: String?)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int

    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
    fun setOnErrorListener(listener: () -> Unit)
}