package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.viewmodel.SearchState
import com.example.playlistmaker.search.viewmodel.SearchViewModel
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private val historyTracks = ArrayList<Track>()
    private lateinit var historyAdapter: TrackAdapter

    private var currentText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews() {
        binding.back.setNavigationOnClickListener {
            finish()
        }

        historyAdapter = TrackAdapter(historyTracks)
        binding.historyList.adapter = historyAdapter

        binding.trackList.adapter = trackAdapter
    }

    private fun setupClickListeners() {
        trackAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                openPlayer(track)
            }
        }

        historyAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                viewModel.addToHistory(track)
                openPlayer(track)
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
        viewModel.state.observe(this) { state ->
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

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(PlayerActivity.Companion.EXTRA_TRACK, track)
        startActivity(intent)
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding.searchText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT, "")
        binding.searchText.setText(restoredText)
    }

    companion object {
        private const val SEARCH_TEXT = "search_text"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}