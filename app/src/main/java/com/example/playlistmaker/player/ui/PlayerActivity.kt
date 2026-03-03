package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.util.getSerializableExtraCompat
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TRACK = "track"
    }

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getSerializableExtraCompat<Track>(EXTRA_TRACK)

        if (track == null) {
            finish()
            return
        }

        initViewModel(track)
        initViews(track)
        observeViewModel()
    }

    private fun initViewModel(track: Track) {
        val playerInteractor = Creator.providePlayerInteractor()
        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getFactory(playerInteractor, track)
        )[PlayerViewModel::class.java]
    }

    private fun initViews(track: Track) {
        binding.back.setNavigationOnClickListener {
            finish()
        }

        with(binding) {
            trackTitle.text = track.trackName.trim()
            trackArtist.text = track.artistName.trim()
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(this@PlayerActivity)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners((8 * resources.displayMetrics.density).toInt()))
                .into(trackCover)

            if (track.collectionName.isNullOrEmpty()) {
                trackAlbum.visibility = View.GONE
                trackAlbumTitle.visibility = View.GONE
            } else {
                trackAlbum.visibility = View.VISIBLE
                trackAlbumTitle.visibility = View.VISIBLE
                trackAlbum.text = track.collectionName.trim()
            }

            if (track.releaseDate.isNullOrEmpty()) {
                trackYear.visibility = View.GONE
                trackYearTitle.visibility = View.GONE
            } else {
                trackYear.visibility = View.VISIBLE
                trackYearTitle.visibility = View.VISIBLE
                trackYear.text = track.releaseDate.take(4)
            }

            trackGenre.text = track.primaryGenreName.trim()
            trackCountry.text = track.country.trim()

            var isAdded = false
            add.setOnClickListener {
                isAdded = !isAdded
                if (isAdded) {
                    add.setImageResource(R.drawable.ic_added_23)
                } else {
                    add.setImageResource(R.drawable.ic_add_23)
                }
            }

            playBtn.setOnClickListener {
                viewModel.onPlayButtonClicked()
            }

            var isLiked = false
            like.setOnClickListener {
                isLiked = !isLiked
                if (isLiked) {
                    like.setImageResource(R.drawable.ic_like_active_25)
                } else {
                    like.setImageResource(R.drawable.ic_like_25)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            binding.playBtn.isEnabled = state.isPlayButtonEnabled
            binding.playBtn.setImageResource(
                if (state.isPlaying) R.drawable.ic_pause_100
                else R.drawable.ic_play_100
            )
            binding.playTime.text = state.currentTime
        }
    }

    private fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}