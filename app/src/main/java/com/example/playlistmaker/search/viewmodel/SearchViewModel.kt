package com.example.playlistmaker.search.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData(SearchState())

    val state: LiveData<SearchState> = _state

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    init {
        loadHistory()
    }

    private fun loadHistory() {
        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(history: Result<List<Track>>) {
                history.fold(
                    onSuccess = { tracks ->
                        _state.postValue(_state.value?.copy(
                            showHistory = true,
                            historyTracks = tracks,
                            historyEmpty = tracks.isEmpty()
                        ))
                    },
                    onFailure = { exception ->
                        _state.postValue(_state.value?.copy(
                            showHistory = true,
                            historyTracks = emptyList(),
                            historyEmpty = true
                        ))
                    }
                )
            }
        })
    }

    fun searchDebounce(query: String) {
        if (query.isEmpty()) {
            loadHistory()
            return
        }

        _state.value = _state.value?.copy(
            isLoading = true,
            showHistory = false
        )

        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            searchTracks(query)
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks(query: String) {
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: Result<List<Track>>) {
                result.fold(
                    onSuccess = { tracks ->
                        _state.postValue (_state.value?.copy(
                            isLoading = false,
                            tracks = tracks,
                            isEmpty = tracks.isEmpty(),
                            isError = false
                        ))
                    },
                    onFailure = {
                        _state.postValue (_state.value?.copy(
                            isLoading = false,
                            isError = true,
                            isEmpty = false,
                            tracks = emptyList()
                        ))
                    }
                )
            }
        })
    }

    fun addToHistory(track: Track) {
        searchHistoryInteractor.addToHistory(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        loadHistory()
    }

    fun onSearchCleared() {
        loadHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}

class SearchViewModelFactory(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(tracksInteractor, searchHistoryInteractor) as T
    }
}

data class SearchState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val showHistory: Boolean = true,
    val historyTracks: List<Track> = emptyList(),
    val historyEmpty: Boolean = true
)