package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.viewmodel.FavoriteViewModel
import com.example.playlistmaker.media.viewmodel.PlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {

    private val playlistViewModel: PlaylistViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme(darkTheme = false) {
                    MediaScreen(
                        favoriteViewModel = favoriteViewModel,
                        playlistViewModel = playlistViewModel,
                        onTrackClick = { track ->
                            findNavController().navigate(
                                R.id.action_global_playerFragment,
                                PlayerFragment.createArgs(track)
                            )
                        },
                        onPlaylistClick = { playlistId ->
                            findNavController().navigate(
                                R.id.action_mediaFragment_to_playlistDetailsFragment,
                                PlaylistDetailsFragment.createArgs(playlistId)
                            )
                        },
                        onNewPlaylistClick = {
                            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
                        }
                    )
                }
            }
        }
    }
}