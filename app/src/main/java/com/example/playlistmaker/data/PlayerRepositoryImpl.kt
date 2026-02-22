package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerRepository
import java.io.IOException

class PlayerRepositoryImpl () : PlayerRepository {
    private var mediaPlayer = MediaPlayer()
    private var preparedListener: (() -> Unit)? = null
    private var completionListener: (() -> Unit)? = null
    private var errorListener: (() -> Unit)? = null

    override fun prepare(url: String?) {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                preparedListener?.invoke()
            }
            mediaPlayer.setOnCompletionListener {
                completionListener?.invoke()
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                errorListener?.invoke()
                true
            }
        } catch (e: IOException) {
            errorListener?.invoke()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying() : Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition() : Int {
        return mediaPlayer.currentPosition
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