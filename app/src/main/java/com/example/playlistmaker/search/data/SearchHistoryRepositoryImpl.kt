package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.StorageClient
import com.example.playlistmaker.search.data.dto.TrackEntity
import com.example.playlistmaker.search.data.dto.toDomain
import com.example.playlistmaker.search.data.dto.toEntity
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl (private val storage: StorageClient<ArrayList<TrackEntity>>) :
    SearchHistoryRepository {
        override fun getHistory(): Result<List<Track>> {
                val entities = storage.getData() ?: arrayListOf()
                val track = entities.map { it.toDomain() }
                return Result.success(track)
        }

        override fun clearHistory() {
            storage.storeData(arrayListOf())
        }

        override fun addToHistory(track: Track) {

            val entities = storage.getData() ?: arrayListOf()
            val entity = track.toEntity()

            entities.removeAll { it.trackId == entity.trackId }
            entities.add(0, entity)

            val updatedHistory = if (entities.size > 10) {
                ArrayList(entities.subList(0, 10))
            } else {
                entities
            }

            storage.storeData(updatedHistory)
        }
}