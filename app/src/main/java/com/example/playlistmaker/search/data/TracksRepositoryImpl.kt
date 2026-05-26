package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.data.dto.toTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Result<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 200) {
            with(response as TrackSearchResponse) {
                val tracks = results.map { trackDto ->
                    trackDto.toTrack()
                }
                emit(Result.success(tracks))
            }
        } else {
            emit(Result.failure(Exception("Ошибка сервера: ${response.resultCode}")))
        }
    }.flowOn(Dispatchers.IO)
}