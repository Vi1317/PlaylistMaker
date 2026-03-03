package com.example.playlistmaker.player.domain

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: (() -> Unit)? = null

    init {
        repository.setOnPreparedListener {
            onPreparedListener?.invoke()
        }

        repository.setOnCompletionListener {
            onCompletionListener?.invoke()
        }

        repository.setOnErrorListener {
            onErrorListener?.invoke()
        }
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    override fun setOnErrorListener(listener: () -> Unit) {
        onErrorListener = listener
    }

    override fun prepare(url: String?) {
        repository.prepare(url)
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun release() {
        repository.release()
    }
}