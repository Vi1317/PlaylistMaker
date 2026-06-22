package com.example.playlistmaker.media.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.viewmodel.PlaylistDetailsViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.showCustomToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {
    private val playlistId: Long by lazy {
        requireArguments().getLong(ARG_PLAYLIST_ID)
    }

    private val viewModel: PlaylistDetailsViewModel by viewModel { parametersOf(playlistId) }
    private lateinit var binding: FragmentPlaylistDetailsBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var trackAdapter: TrackAdapter
    private val tracks = mutableListOf<Track>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (menuBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        binding.overlay.visibility = View.GONE
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        )

        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        val expectedPeek = binding.root.height - binding.bottomSheetAnchor.bottom
                        if (peekHeight < expectedPeek) {
                            setPeekHeight(expectedPeek, false)
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        updateBottomSheetPeekHeight()

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet.root).apply {
            isHideable = true
            skipCollapsed = false
            state = BottomSheetBehavior.STATE_HIDDEN

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        binding.overlay.visibility = View.GONE
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        val overlay = binding.overlay

        overlay.setOnClickListener {
            hideMenuBottomSheet()
        }

        trackAdapter = TrackAdapter(tracks)
        trackAdapter.onTrackClick = { track ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        trackAdapter.onTrackLongClick = { track ->
            showDeleteTrackDialog(track)
        }

        binding.tracksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecycler.adapter = trackAdapter

        binding.back.setOnClickListener {
            if (menuBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                hideMenuBottomSheet()
            } else {
                findNavController().navigateUp()
            }
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.actions.setOnClickListener {
            showMenu()
        }

        binding.menuBottomSheet.menuShare.setOnClickListener {
            hideMenuBottomSheet()
            sharePlaylist()
        }

        binding.menuBottomSheet.menuEdit.setOnClickListener {
            hideMenuBottomSheet()
            val currentPlaylist = viewModel.state.value?.playlist
            currentPlaylist?.let {
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                    NewPlaylistFragment.createArgs(it)
                )
            }
        }

        binding.menuBottomSheet.menuDelete.setOnClickListener {
            hideMenuBottomSheet()
            val currentPlaylist = viewModel.state.value?.playlist
            currentPlaylist?.let { showDeletePlaylistDialog(it) }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.playlist?.let { playlist ->
                updatePlaylistInfo(playlist, state.totalDuration, state.trackCount)
                updateMenuInfo(playlist)
            }
            updateTracks(state.tracks)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylistData()
    }

    private fun showMenu() {
        binding.overlay.apply {
            alpha = 1f
            visibility = View.VISIBLE
            isClickable = true
            isFocusable = true
        }
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun updateMenuInfo(playlist: Playlist) {
        binding.menuBottomSheet.playlistName.text = playlist.name
        val trackCountText = resources.getQuantityString(R.plurals.track_count, playlist.trackCount, playlist.trackCount)
        binding.menuBottomSheet.trackCount.text = trackCountText

        if (playlist.coverPath.isNotEmpty()) {
            Glide.with(this)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.menuBottomSheet.playlistCover)
        } else {
            binding.menuBottomSheet.playlistCover.setImageResource(R.drawable.placeholder)
        }
    }

    private fun sharePlaylist() {
        val currentState = viewModel.state.value
        val playlist = currentState?.playlist ?: return
        val tracks = currentState.tracks

        if (tracks.isEmpty()) {
            showCustomToast(
                requireContext(),
                getString(R.string.empty_playlist)
            )
            return
        }

        val message = buildShareMessage(playlist, tracks)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.playlist_share_text)))
    }

    private fun buildShareMessage(playlist: Playlist, tracks: List<Track>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine(playlist.name)
        if (playlist.description.isNotEmpty()) {
            stringBuilder.appendLine(playlist.description)
        }
        stringBuilder.appendLine()
        stringBuilder.appendLine(getTrackCountWord(playlist.trackCount))
        stringBuilder.appendLine()

        tracks.forEachIndexed { index, track ->
            val duration = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())
                .format(track.trackTimeMillis)
            stringBuilder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
        }

        return stringBuilder.toString()
    }

    private fun getTrackCountWord(count: Int): String {
        return resources.getQuantityString(R.plurals.track_count, count, count)
    }

    private fun updateBottomSheetPeekHeight() {
        binding.root.doOnLayout {
            val anchorBottom = binding.bottomSheetAnchor.bottom
            val screenHeight = binding.root.height
            val peekHeight = screenHeight - anchorBottom

            bottomSheetBehavior.peekHeight = peekHeight.coerceAtLeast(0)
        }
    }

    private fun hideMenuBottomSheet() {
        if (menuBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE
        }
    }

    private fun updatePlaylistInfo(playlist: Playlist, totalDuration: String, trackCount: String) {
        binding.playlistName.text = playlist.name

        if (playlist.description.isNotEmpty()) {
            binding.playlistDescription.text = playlist.description
            binding.playlistDescription.visibility = View.VISIBLE
        } else {
            binding.playlistDescription.visibility = View.GONE
        }

        binding.playlistDuration.text = totalDuration
        binding.playlistTrackCount.text = trackCount

        if (playlist.coverPath.isNotEmpty()) {
            Glide.with(this)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder)
        }

        updateBottomSheetPeekHeight()
    }

    private fun updateTracks(tracks: List<Track>) {
        trackAdapter.updateTracks(tracks)

        if (tracks.isEmpty()) {
            binding.tracksRecycler.visibility = View.GONE
            binding.emptyPlaylistText.visibility = View.VISIBLE
        } else {
            binding.tracksRecycler.visibility = View.VISIBLE
            binding.emptyPlaylistText.visibility = View.GONE
        }
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(R.string.playlist_delete)
            .setMessage(getString(R.string.delete_playlist_title, playlist.name))
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
                requireActivity().lifecycleScope.launch {
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
            }
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        AlertDialog.Builder(requireContext(),R.style.CustomAlertDialogTheme)
            .setTitle(R.string.delete_track_title)
            .setMessage("${track.trackName} - ${track.artistName}")
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrackFromPlaylist(track.trackId)
            }
            .show()
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle {
            return Bundle().apply {
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
        }
    }
}