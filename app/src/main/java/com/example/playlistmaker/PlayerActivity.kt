package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TRACK = "track"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3


        private const val UPDATE_DEBOUNCE_DELAY = 300L
    }

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var play: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var likeButton: ImageButton

    private lateinit var url: String
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playRunnable: Runnable
    private lateinit var playTime: TextView
    private lateinit var trackTimeMillis: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_TRACK) as? Track
        }

        if (track == null) {
            finish()
            return
        }

        url = track.previewUrl

        mediaPlayer = MediaPlayer()

        initRunnable()

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val trackName = findViewById<TextView>(R.id.track_title)
        val artistName = findViewById<TextView>(R.id.track_artist)
        trackTimeMillis = findViewById<TextView>(R.id.track_time)
        val trackCover = findViewById<ImageView>(R.id.track_cover)
        val albumName = findViewById<TextView>(R.id.track_album)
        val year = findViewById<TextView>(R.id.track_year)
        val genre = findViewById<TextView>(R.id.track_genre)
        val country = findViewById<TextView>(R.id.track_country)

        trackName.text = track.trackName.trim()
        artistName.text = track.artistName.trim()
        trackTimeMillis.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).trim()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners((8 * resources.displayMetrics.density).toInt()))
            .into(trackCover)
        playTime = findViewById<TextView>(R.id.play_time)

        val albumTitle = findViewById<TextView>(R.id.track_album_title)
        if (track.collectionName.trim().isEmpty()) {
            albumName.visibility = View.GONE
            albumTitle.visibility = View.GONE
        } else {
            albumName.visibility = View.VISIBLE
            albumTitle.visibility = View.VISIBLE
            albumName.text = track.collectionName.trim()
        }

        val yearTitle = findViewById<TextView>(R.id.track_year_title)
        if (track.releaseDate.trim().isEmpty()) {
            year.visibility = View.GONE
            yearTitle.visibility = View.GONE
        } else {
            year.visibility = View.VISIBLE
            yearTitle.visibility = View.VISIBLE
            year.text = track.releaseDate.take(4)
        }

        genre.text = track.primaryGenreName.trim()
        country.text = track.country.trim()

        addButton = findViewById<ImageButton>(R.id.add)
        var isAdded = false
        addButton.setOnClickListener {
            isAdded = !isAdded
            if (isAdded) {
                addButton.setImageResource(R.drawable.ic_added_23)
            } else {
                addButton.setImageResource(R.drawable.ic_add_23)
            }
        }

        play = findViewById<ImageButton>(R.id.play_btn)
        play.setOnClickListener {
            playbackControl()
        }

        likeButton = findViewById<ImageButton>(R.id.like)
        var isLiked = false
        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_like_active_25)
            } else {
                likeButton.setImageResource(R.drawable.ic_like_25)
            }
        }

        preparePlayer()
    }

    fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    private fun preparePlayer() {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                play.isEnabled = true
                playerState = STATE_PREPARED
                resetPlayTime()
            }
            mediaPlayer.setOnCompletionListener {
                stopTimer()
                resetPlayTime()
                play.setImageResource(R.drawable.ic_play_100)
                play.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnErrorListener { mp, what, extra ->
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        stopTimer()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                play.setImageResource(R.drawable.ic_play_100)
                play.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                play.setImageResource(R.drawable.ic_pause_100)
                play.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
            play.setImageResource(R.drawable.ic_play_100)
            play.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun startTimer() {
        handler.removeCallbacks(playRunnable)
        handler.post(playRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(playRunnable)
    }

    private fun resetPlayTime() {
        playTime.text = "00:00"
    }

    private fun initRunnable() {
        playRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    playTime.text = SimpleDateFormat("mm:ss",Locale.getDefault()).format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, UPDATE_DEBOUNCE_DELAY)
                }
            }
        }
    }

}