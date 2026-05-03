package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {
    companion object {
        const val TAG = "PlayerFragment"
        const val EXTRA_TRACK = "track"
        fun createArgs(track: Track): Bundle {
            return Bundle().apply {
                putSerializable(EXTRA_TRACK, track)
            }
        }
    }
    private val track: Track by lazy {
        @Suppress("DEPRECATION")
        requireArguments().getSerializable(EXTRA_TRACK) as Track
    }
    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        initViews(track)
        observeViewModel()
    }

    private fun initViews(track: Track) {
        with(binding) {
            trackTitle.text = track.trackName.trim()
            trackArtist.text = track.artistName.trim()
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

            Glide.with(this@PlayerFragment)
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
        viewModel.state.observe(viewLifecycleOwner) { state ->
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