package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private lateinit var searchHistory: SearchHistory
    private val historyTracks = ArrayList<Track>()
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = getSharedPreferences(
            "app_preferences",
            MODE_PRIVATE
        )
        searchHistory = SearchHistory(sharedPreferences)

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            finish()
        }

        historyLayout = findViewById<LinearLayout>(R.id.history)
        clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        historyList = findViewById<RecyclerView>(R.id.history_list)
        historyAdapter = TrackAdapter(historyTracks)
        historyList.adapter = historyAdapter
        historyAdapter.onTrackClick = {
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.EXTRA_TRACK, it)
                startActivity(intent)
            }
        }

        trackList = findViewById<RecyclerView>(R.id.track_list)
        trackList.adapter = trackAdapter
        trackAdapter.onTrackClick = {
            if (clickDebounce()) {
                searchHistory.write(it)

                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.EXTRA_TRACK, it)
                startActivity(intent)
            }
        }

        searchText = findViewById<EditText>(R.id.search_text)
        clearButton = findViewById<ImageView>(R.id.clear_search)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        notConnect = findViewById<LinearLayout>(R.id.connection_error)
        notFound = findViewById<LinearLayout>(R.id.not_found_error)
        retryButton = findViewById<Button>(R.id.retry_button)

        clearButton.setOnClickListener {
            searchText.setText("")
            clearButton.visibility = View.GONE
            trackList.visibility = View.GONE
            notConnect.visibility = View.GONE
            notFound.visibility = View.GONE
            showHistory()

            closeKeyboard()
        }

        searchText.addTextChangedListener { text ->
            clearButton.visibility = clearButtonVisibility(text)
            currentText = text?.toString() ?: ""

            searchDebounce()

            if (currentText != "") {
                historyLayout.visibility = View.GONE
            } else {
                showHistory()
            }
        }

        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (currentText.isNotEmpty()) {
                    searchTracks()
                }
                closeKeyboard()
                true
            }
            false
        }

        retryButton.setOnClickListener {
            if (currentText.isNotEmpty()) {
                searchTracks()
            }
        }

        showHistory()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val SEARCH_TEXT = "search_text"
        private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val searchRunnable = Runnable { searchTracks() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    private fun searchTracks() {
        if (searchText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            tracks.clear()

            iTunesService.search(searchText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse?>,
                        response: Response<TracksResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val searchResults = response.body()
                            tracks.clear()
                            searchResults?.results?.forEach { result ->
                                tracks.add(
                                    Track(
                                        trackId = result.trackId,
                                        trackName = result.trackName,
                                        artistName = result.artistName,
                                        trackTimeMillis = result.trackTimeMillis,
                                        artworkUrl100 = result.artworkUrl100,
                                        collectionName = result.collectionName,
                                        releaseDate = result.releaseDate,
                                        primaryGenreName = result.primaryGenreName,
                                        country = result.country,
                                        previewUrl = result.previewUrl
                                    )
                                )
                            }
                            trackAdapter.notifyDataSetChanged()

                            if (tracks.isEmpty()) {
                                showNotFoundError()
                            } else {
                                progressBar.visibility = View.GONE
                                showSearchResults()
                            }
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse?>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        tracks.clear()
                        trackAdapter.notifyDataSetChanged()
                        showConnectionError()
                    }

                })
        }
    }

    private fun showHistory() {
        tracks.clear()
        val historyItems = searchHistory.read()
        if (!historyItems.isEmpty()) {
            notConnect.visibility = View.GONE
            notFound.visibility = View.GONE
            trackList.visibility = View.GONE
            historyLayout.visibility = View.VISIBLE

            historyTracks.addAll(historyItems)
            trackAdapter.notifyDataSetChanged()
        } else {
            historyLayout.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            tracks.clear()
            searchHistory.clear()
            trackAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }
    }

    private fun showSearchResults() {
        notConnect.visibility = View.GONE
        notFound.visibility = View.GONE
        historyLayout.visibility = View.GONE
        trackList.visibility = View.VISIBLE
    }

    private fun showNotFoundError() {
        notConnect.visibility = View.GONE
        notFound.visibility = View.VISIBLE
        trackList.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun showConnectionError() {
        notConnect.visibility = View.VISIBLE
        notFound.visibility = View.GONE
        trackList.visibility = View.GONE
        historyLayout.visibility = View.GONE
        closeKeyboard()
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(searchText.windowToken, 0)
        }
    }
}