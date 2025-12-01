package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
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

    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private lateinit var searchText: EditText
    private var currentText: String = ""
    private lateinit var clearButton: ImageView
    private lateinit var notFound: LinearLayout
    private lateinit var notConnect: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var trackList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        trackList = findViewById<RecyclerView>(R.id.track_list)
        trackList.adapter = trackAdapter

        searchText = findViewById<EditText>(R.id.search_text)
        clearButton = findViewById<ImageView>(R.id.clear_search)

        notConnect = findViewById<LinearLayout>(R.id.connection_error)
        notFound = findViewById<LinearLayout>(R.id.not_found_error)
        retryButton = findViewById<Button>(R.id.retry_button)

        clearButton.setOnClickListener {
            searchText.setText("")
            clearButton.visibility = View.GONE
            trackList.visibility = View.GONE
            notConnect.visibility = View.GONE
            notFound.visibility = View.GONE

            closeKeyboard()
        }

        searchText.addTextChangedListener { text ->
            clearButton.visibility = clearButtonVisibility(text)
            currentText = text?.toString() ?: ""
            trackList.visibility = View.VISIBLE
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
            iTunesService.search(searchText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse?>,
                        response: Response<TracksResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val searchResults = response.body()?.results

                            if (!searchResults.isNullOrEmpty()) {
                                tracks.clear()
                                tracks.addAll(searchResults)
                                trackAdapter.notifyDataSetChanged()
                                showSearchResults()
                            } else {
                                tracks.clear()
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showNotFoundError()
                            }
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse?>, t: Throwable) {
                        tracks.clear()
                        trackAdapter.notifyDataSetChanged()
                        showConnectionError()
                    }

                })
        }
    }

    private fun showSearchResults() {
        notConnect.visibility = View.GONE
        notFound.visibility = View.GONE
        trackList.visibility = View.VISIBLE
        closeKeyboard()
    }

    private fun showNotFoundError() {
        notConnect.visibility = View.GONE
        notFound.visibility = View.VISIBLE
        trackList.visibility = View.GONE
    }

    private fun showConnectionError() {
        notConnect.visibility = View.VISIBLE
        notFound.visibility = View.GONE
        trackList.visibility = View.GONE
        closeKeyboard()
    }

    private fun closeKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(searchText.windowToken, 0)
        }
    }
}