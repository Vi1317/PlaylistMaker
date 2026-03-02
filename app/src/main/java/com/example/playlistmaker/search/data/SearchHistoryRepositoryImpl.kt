package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.Resource
import com.example.playlistmaker.search.data.dto.StorageClient
import com.example.playlistmaker.search.data.dto.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl (private val storage: StorageClient<ArrayList<Track>>) :
    SearchHistoryRepository {
        override fun getHistory(): Resource<List<Track>> {
            val track = storage.getData() ?: listOf()
            return Resource.Success(track)
        }

        override fun clearHistory() {
            storage.storeData(arrayListOf())
        }

        override fun addToHistory(track: Track) {
            val currentHistory = storage.getData() ?: arrayListOf()

            currentHistory.removeAll { it.trackId == track.trackId }

            currentHistory.add(0, track)

            val updatedHistory = if (currentHistory.size > 10) {
                currentHistory.subList(0, 10)
            } else {
                currentHistory
            }

            storage.storeData(ArrayList(updatedHistory))
        }
}