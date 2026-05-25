package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null
    private var preparedListener: (() -> Unit)? = null
    private var completionListener: (() -> Unit)? = null
    private var errorListener: (() -> Unit)? = null

    override fun prepare(url: String?) {
        release()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    preparedListener?.invoke()
                }
                setOnCompletionListener {
                    completionListener?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    errorListener?.invoke()
                    true
                }
            }
        } catch (e: IOException) {
            errorListener?.invoke()
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
    }

    override fun isPlaying() : Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun getCurrentPosition() : Int {
        return try {
            mediaPlayer?.currentPosition ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        preparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        completionListener = listener
    }

    override fun setOnErrorListener(listener: () -> Unit) {
        errorListener = listener
    }
}