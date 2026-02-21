package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.data.dto.toTrack
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Result<List<Track>> {
        return try {
            val response = networkClient.doRequest(TrackSearchRequest(expression))

            if (response.resultCode == 200) {
                if (response is TrackSearchResponse) {
                    val tracks = response.results.map { trackDto ->
                        trackDto.toTrack()
                    }
                    Result.success(tracks)
                } else {
                    Result.failure(Exception("Неверный формат ответа"))
                }
            } else {
                Result.failure(Exception("Ошибка сервера: ${response.resultCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}