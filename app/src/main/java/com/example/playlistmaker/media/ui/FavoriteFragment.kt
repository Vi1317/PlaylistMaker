package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteBinding
import com.example.playlistmaker.media.viewmodel.FavoriteViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteFragment : Fragment() {
    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private lateinit var binding: FragmentFavoriteBinding

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteList.layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteList.adapter = adapter

        adapter.onTrackClick = { track ->
            findNavController().navigate(
                R.id.action_global_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        favoriteViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteViewModel.FavoriteState.Empty -> {
                    binding.notFoundError.visibility = View.VISIBLE
                    binding.favoriteList.visibility = View.GONE
                }
                is FavoriteViewModel.FavoriteState.Content -> {
                    binding.notFoundError.visibility = View.GONE
                    binding.favoriteList.visibility = View.VISIBLE
                    updateTracks(state.tracks)
                }
            }
        }

        favoriteViewModel.loadFavorites()
    }

    private fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        favoriteViewModel.loadFavorites()
    }
}