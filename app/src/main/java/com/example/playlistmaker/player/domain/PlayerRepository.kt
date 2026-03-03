package com.example.playlistmaker.player.domain

interface PlayerRepository {
    fun prepare(url: String?)
    fun start()
    fun pause()
    fun release()
    fun isPlaying() : Boolean
    fun getCurrentPosition() : Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
    fun setOnErrorListener(listener: () -> Unit)
}