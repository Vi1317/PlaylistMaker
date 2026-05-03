package com.example.playlistmaker.search.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData(SearchState())

    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null

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

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(query)
        }
    }

    private suspend fun searchRequest(query: String) {
        _state.value = _state.value?.copy(
            isLoading = true,
            showHistory = false
        )

        tracksInteractor.searchTracks(query).collect { pair ->
            processResult(pair.first, pair.second)
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = true,
                    isEmpty = false,
                    tracks = emptyList()
                )
            }
            foundTracks.isNullOrEmpty() -> {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = false,
                    isEmpty = true,
                    tracks = emptyList()
                )
            }
            else -> {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = false,
                    isEmpty = false,
                    tracks = foundTracks
                )
            }
        }
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

data class SearchState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val showHistory: Boolean = true,
    val historyTracks: List<Track> = emptyList(),
    val historyEmpty: Boolean = true
)