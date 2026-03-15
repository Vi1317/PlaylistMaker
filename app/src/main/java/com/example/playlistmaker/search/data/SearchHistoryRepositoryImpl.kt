package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.toDomain
import com.example.playlistmaker.search.data.dto.toEntity
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl (private val storage: SearchHistoryStorage) :
    SearchHistoryRepository {
        override fun getHistory(): Result<List<Track>> {
                val entities = storage.getData()
                val track = entities.map { it.toDomain() }
                return Result.success(track)
        }

        override fun clearHistory() {
            storage.clearData()
        }

        override fun addToHistory(track: Track) {

            val entities = storage.getData().toMutableList()
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