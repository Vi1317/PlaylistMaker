package com.example.playlistmaker.search.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.db.FavoriteInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _state = MutableLiveData(SearchState())
    val state: LiveData<SearchState> = _state

    private var searchJob: Job? = null

    private var currentSearchQuery: String? = null

    private var cachedFavoriteIds = emptyList<Int>()

    init {
        loadHistory()
        loadFavorites()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val updatedHistory = withContext(Dispatchers.IO) {
                val history = searchHistoryInteractor.getHistory()
                history.map { track ->
                    track.copy(
                        isFavorite = cachedFavoriteIds.contains(track.trackId)
                    )
                }
            }
            _state.value = _state.value?.copy(
                showHistory = true,
                historyTracks = updatedHistory,
                historyEmpty = updatedHistory.isEmpty()
            )
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedFavoriteIds = favoriteInteractor.getFavoriteIds()
        }
    }

    fun searchDebounce(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            currentSearchQuery = null
            loadHistory()
            return
        }

        currentSearchQuery = query

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(query)
        }
    }

    private suspend fun searchRequest(query: String) {
        _state.value = _state.value?.copy(
            isLoading = true,
            showHistory = false,
            isError = false,
            isEmpty = false
        )

        try {
            val resultPair = tracksInteractor.searchTracks(query).first()

            if (query != currentSearchQuery) return

            val (tracks, errorMessage) = resultPair
            processResult(tracks, errorMessage, query)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (query == currentSearchQuery) {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = true,
                    tracks = emptyList()
                )
            }
        }
    }

    private suspend fun processResult(
        foundTracks: List<Track>?,
        errorMessage: String?,
        query: String
    ) {
        if (query != currentSearchQuery) return

        when {
            errorMessage != null -> {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = true,
                    isEmpty = false,
                    showHistory = false,
                    tracks = emptyList()
                )
            }

            foundTracks.isNullOrEmpty() -> {
                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = false,
                    isEmpty = true,
                    showHistory = false,
                    tracks = emptyList()
                )
            }

            else -> {
                val favoriteIds = withContext(Dispatchers.IO) {
                    favoriteInteractor.getFavoriteIds()
                }

                if (query != currentSearchQuery) return

                val updatedTracks = foundTracks.map { track ->
                    track.copy(isFavorite = favoriteIds.contains(track.trackId))
                }

                _state.value = _state.value?.copy(
                    isLoading = false,
                    isError = false,
                    isEmpty = false,
                    showHistory = false,
                    tracks = updatedTracks
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 500L
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