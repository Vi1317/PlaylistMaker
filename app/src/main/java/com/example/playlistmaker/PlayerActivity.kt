package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TRACK = "track"
    }

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

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val trackName = findViewById<TextView>(R.id.track_title)
        val artistName = findViewById<TextView>(R.id.track_artist)
        val trackTimeMillis = findViewById<TextView>(R.id.track_time)
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

        val addButton = findViewById<ImageButton>(R.id.add)
        var isAdded = false
        addButton.setOnClickListener {
            isAdded = !isAdded
            if (isAdded) {
                addButton.setImageResource(R.drawable.ic_added_23)
            } else {
                addButton.setImageResource(R.drawable.ic_add_23)
            }
        }

        val playButton = findViewById<ImageButton>(R.id.play_btn)
        var isPlaying = false
        playButton.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                playButton.setImageResource(R.drawable.ic_pause_100)
                playButton.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
            } else {
                playButton.setImageResource(R.drawable.ic_play_100)
                playButton.setColorFilter(ContextCompat.getColor(this, R.color.play_btn))
            }
        }

        val likeButton = findViewById<ImageButton>(R.id.like)
        var isLiked = false
        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_like_active_25)
            } else {
                likeButton.setImageResource(R.drawable.ic_like_25)
            }
        }
    }

    fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}