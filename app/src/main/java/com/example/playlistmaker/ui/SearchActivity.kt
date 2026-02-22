package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.SearchHistoryManager
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.Creator
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var searchRunnable: Runnable? = null

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private val historyTracks = ArrayList<Track>()
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var searchText: EditText
    private var currentText: String = ""
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var notFound: LinearLayout
    private lateinit var notConnect: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var trackList: RecyclerView

    private lateinit var historyLayout: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var historyList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initDependencies()
        initViews()
        setupClickListeners()

        showHistory()
    }

    private fun initDependencies() {
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
    }

    private fun initViews() {
        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            finish()
        }

        historyLayout = findViewById(R.id.history)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyList = findViewById(R.id.history_list)

        historyAdapter = TrackAdapter(historyTracks)
        historyList.adapter = historyAdapter

        trackList = findViewById(R.id.track_list)
        trackList.adapter = trackAdapter

        searchText = findViewById(R.id.search_text)
        clearButton = findViewById(R.id.clear_search)

        progressBar = findViewById(R.id.progressBar)
        notConnect = findViewById(R.id.connection_error)
        notFound = findViewById(R.id.not_found_error)
        retryButton = findViewById(R.id.retry_button)
    }

    private fun setupClickListeners() {
        trackAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.add(track)
                openPlayer(track)
            }
        }

        historyAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.add(track)
                openPlayer(track)
            }
        }

        clearButton.setOnClickListener {
            searchText.setText("")
            clearButton.visibility = View.GONE
            showHistory()
            closeKeyboard()
        }

        searchText.addTextChangedListener { text ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            currentText = text?.toString() ?: ""

            if (currentText.isNotEmpty()) {
                historyLayout.visibility = View.GONE
                searchDebounce()
            } else {
                showHistory()
            }
        }

        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (currentText.isNotEmpty()) {
                    searchTracks(currentText)
                }
                closeKeyboard()
                true
            }
            false
        }

        retryButton.setOnClickListener {
            if (currentText.isNotEmpty()) {
                searchTracks(currentText)
            }
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clear()
            showHistory()
        }
    }

    private fun showHistory() {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()

        val history = searchHistoryInteractor.get()

        if (history.isNotEmpty()) {
            historyTracks.clear()
            historyTracks.addAll(history)
            historyAdapter.notifyDataSetChanged()

            historyLayout.visibility = View.VISIBLE
            trackList.visibility = View.GONE
            notConnect.visibility = View.GONE
            notFound.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            historyLayout.visibility = View.GONE
            trackList.visibility = View.GONE
            notConnect.visibility = View.GONE
            notFound.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun searchTracks(query: String) {
        progressBar.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        trackList.visibility = View.GONE
        notConnect.visibility = View.GONE
        notFound.visibility = View.GONE

        tracks.clear()
        trackAdapter.notifyDataSetChanged()

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: Result<List<Track>>) {
                handler.post {
                    progressBar.visibility = View.GONE

                    result.fold(
                        onSuccess = { foundTracks ->
                            if (foundTracks.isEmpty()) {
                                showNotFoundError()
                            } else {
                                tracks.addAll(foundTracks)
                                trackAdapter.notifyDataSetChanged()
                                showSearchResults()
                            }
                        },
                        onFailure = {
                            showConnectionError()
                        }
                    )
                }
            }
        })
    }

    private fun showSearchResults() {
        trackList.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        notConnect.visibility = View.GONE
        notFound.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showNotFoundError() {
        trackList.visibility = View.GONE
        historyLayout.visibility = View.GONE
        notConnect.visibility = View.GONE
        notFound.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showConnectionError() {
        trackList.visibility = View.GONE
        historyLayout.visibility = View.GONE
        notConnect.visibility = View.VISIBLE
        notFound.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun searchDebounce() {
        searchRunnable?.let { handler.removeCallbacks(it) }

        searchRunnable = Runnable {
            searchTracks(currentText)
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
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
        intent.putExtra(PlayerActivity.EXTRA_TRACK, track)
        startActivity(intent)
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(searchText.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT, "")
        searchText.setText(restoredText)
    }

    companion object {
        private const val SEARCH_TEXT = "search_text"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}