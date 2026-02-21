package com.example.playlistmaker.data.dto

import android.media.MediaPlayer
import java.io.IOException

class PlayerManager {
    private var mediaPlayer = MediaPlayer()
    var onPreparedListener: (() -> Unit)? = null
    var onCompletionListener: (() -> Unit)? = null
    var onErrorListener: (() -> Unit)? = null

    fun prepare(url: String?) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                onPreparedListener?.invoke()
            }
            mediaPlayer.setOnCompletionListener {
                onCompletionListener?.invoke()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                onErrorListener?.invoke()
                true
            }
        } catch (e: IOException) {
            onErrorListener?.invoke()
        }
    }

    fun start() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun release() {
        mediaPlayer.release()
    }

    fun isPlaying() : Boolean {
        return mediaPlayer.isPlaying
    }

    fun getCurrentPosition() : Int {
        return mediaPlayer.currentPosition
    }
}