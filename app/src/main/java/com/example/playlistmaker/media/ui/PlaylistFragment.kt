package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val playlistViewModel: PlaylistViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistAdapter = PlaylistAdapter(emptyList()) { playlist ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistDetailsFragment,
                PlaylistDetailsFragment.createArgs(playlist.id)
            )
        }

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = playlistAdapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        playlistViewModel.state.observe(viewLifecycleOwner) {state ->
            when (state) {
                is PlaylistViewModel.PlaylistState.Empty -> {
                    binding.notFoundError.visibility = View.VISIBLE
                    binding.playlistsRecyclerView.visibility = View.GONE
                }

                is PlaylistViewModel.PlaylistState.Content -> {
                    binding.notFoundError.visibility = View.GONE
                    binding.playlistsRecyclerView.visibility = View.VISIBLE
                    updatePlaylists(state.playlists)
                }
            }
        }
    }

    private fun updatePlaylists(playlists: List<Playlist>) {
        playlistAdapter.updatePlaylists(playlists)
    }

    override fun onResume() {
        super.onResume()
        playlistViewModel.loadPlaylists()
    }
}