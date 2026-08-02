package com.example.playlistmaker.player.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.PlayerInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService : Service(), PlayerInteractor {

    private val binder = MusicServiceBinder()

    private val _playerState = MutableStateFlow(PlayerServiceState(false, "00:00", false))
    val playerState = _playerState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var timerJob: Job? = null
    private var isPlaying = false
    private var isActuallyInForeground = false

    private var artistName = ""
    private var trackTitle = ""

    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null
    private var onErrorListener: (() -> Unit)? = null

    private val notificationId = 1
    private val channelId = "music_service_channel"

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        intent?.getStringExtra("artist")?.let { artistName = it }
        intent?.getStringExtra("title")?.let { trackTitle = it }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        release()
        return super.onUnbind(intent)
    }

    private fun initMediaPlayer(url: String?) {
        if (url.isNullOrEmpty()) return

        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            onPreparedListener?.invoke()
            _playerState.value = _playerState.value.copy(isPrepared = true)
        }
        mediaPlayer?.setOnCompletionListener {
            onCompletionListener?.invoke()
            isPlaying = false
            timerJob?.cancel()
            hideForegroundNotification()
            _playerState.value = PlayerServiceState(false, "00:00", true)
        }
        mediaPlayer?.setOnErrorListener { _, _, _ ->
            onErrorListener?.invoke()
            true
        }
    }

    override fun start() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            isPlaying = true
            startTimer()
            _playerState.value = _playerState.value.copy(isPlaying = true)
        }
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
            timerJob?.cancel()
            _playerState.value = _playerState.value.copy(isPlaying = false)
        }
    }

    override fun prepare(url: String?) {
        initMediaPlayer(url)
    }

    override fun release() {
        timerJob?.cancel()

        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.setOnErrorListener(null)

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        isPlaying = false
        isActuallyInForeground = false
        hideForegroundNotification()
        _playerState.value = PlayerServiceState(false, "00:00", false)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(500L)
                val positionMs = getCurrentPosition().toLong()
                _playerState.value = _playerState.value.copy(
                    currentTime = timeFormatter.format(positionMs)
                )
            }
        }
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    private val timeFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    override fun setOnErrorListener(listener: () -> Unit) {
        onErrorListener = listener
    }

    fun setTrackInfo(artist: String, title: String) {
        artistName = artist
        trackTitle = title
    }

    fun showForegroundNotification() {
        if (!isPlaying) return

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playlist Maker")
            .setContentText("$artistName - $trackTitle")
            .setSmallIcon(R.drawable.ic_media_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
        isActuallyInForeground = true
    }

    fun hideForegroundNotification() {
        if (!isActuallyInForeground) return
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isActuallyInForeground = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            channelId,
            "Music service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifications for audio playback"
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    inner class MusicServiceBinder : Binder() {
        fun getPlayerInteractor(): PlayerInteractor = this@MusicService
        fun getService(): MusicService = this@MusicService
    }
}

data class PlayerServiceState(
    val isPlaying: Boolean,
    val currentTime: String,
    val isPrepared: Boolean
)
