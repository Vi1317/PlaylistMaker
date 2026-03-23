package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.viewmodel.SearchState
import com.example.playlistmaker.search.viewmodel.SearchViewModel
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private val historyTracks = ArrayList<Track>()
    private lateinit var historyAdapter: TrackAdapter

    private var currentText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupClickListeners()
        observeViewModel()

        binding.searchText.setText(currentText)
    }

    private fun initViews() {
        historyAdapter = TrackAdapter(historyTracks)
        binding.historyList.adapter = historyAdapter

        binding.trackList.adapter = trackAdapter
    }

    private fun setupClickListeners() {
        trackAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }

        historyAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                findNavController().navigate(
                    R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(track)
                )
            }
        }

        binding.clearSearch.setOnClickListener {
            binding.searchText.setText("")
            binding.clearSearch.visibility = View.GONE
            viewModel.onSearchCleared()
            closeKeyboard()
        }

        binding.searchText.addTextChangedListener { text ->
            binding.clearSearch.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            currentText = text?.toString() ?: ""
            viewModel.searchDebounce(currentText)
        }

        binding.searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchText.text.isNotEmpty()) {
                    viewModel.searchDebounce(binding.searchText.text.toString())
                }
                closeKeyboard()
                true
            }
            false
        }

        binding.retryButton.setOnClickListener {
            if (binding.searchText.text.isNotEmpty()) {
                viewModel.searchDebounce(binding.searchText.text.toString())
            }
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        when {
            state.isLoading -> showLoading()
            state.isError -> showConnectionError()
            state.isEmpty -> showNotFoundError()
            state.showHistory -> showHistory(state.historyTracks, state.historyEmpty)
            else -> showSearchResults(state.tracks)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            history.visibility = View.GONE
            connectionError.visibility = View.GONE
            notFoundError.visibility = View.GONE
        }
    }

    private fun showSearchResults(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        trackAdapter.notifyDataSetChanged()

        binding.apply {
            progressBar.visibility = View.GONE
            trackList.visibility = View.VISIBLE
            history.visibility = View.GONE
            connectionError.visibility = View.GONE
            notFoundError.visibility = View.GONE
        }
    }

    private fun showHistory(h: List<Track>, isEmpty: Boolean) {
        if (!isEmpty) {
            historyTracks.clear()
            historyTracks.addAll(h)
            historyAdapter.notifyDataSetChanged()

            binding.apply {
                history.visibility = View.VISIBLE
                trackList.visibility = View.GONE
                connectionError.visibility = View.GONE
                notFoundError.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        } else {
            binding.apply {
                history.visibility = View.GONE
                trackList.visibility = View.GONE
                connectionError.visibility = View.GONE
                notFoundError.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showNotFoundError() {
        binding.apply {
            trackList.visibility = View.GONE
            history.visibility = View.GONE
            connectionError.visibility = View.GONE
            notFoundError.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun showConnectionError() {
        binding.apply {
            trackList.visibility = View.GONE
            history.visibility = View.GONE
            connectionError.visibility = View.VISIBLE
            notFoundError.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun closeKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    companion object {
        private const val SEARCH_TEXT = "search_text"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        const val TAG = "SearchFragment"
    }
}