package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.PlayerManager
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl (private val playerManager: PlayerManager) : PlayerRepository {
    override fun prepare(url: String?) {
        playerManager.prepare(url)
    }
    override fun start() {
        playerManager.start()
    }
    override fun pause() {
        playerManager.pause()
    }
    override fun release() {
        playerManager.release()
    }
    override fun isPlaying() : Boolean {
        return playerManager.isPlaying()
    }
    override fun getCurrentPosition() : Int{
        return playerManager.getCurrentPosition()
    }
    override fun setOnPreparedListener(listener: () -> Unit) {
        playerManager.onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        playerManager.onCompletionListener = listener
    }

    override fun setOnErrorListener(listener: () -> Unit) {
        playerManager.onErrorListener = listener
    }
}