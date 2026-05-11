package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import kotlin.coroutines.cancellation.CancellationException

class RetrofitNetworkClient(private val iTunesService: ITunesApi) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return try {
            val response = iTunesService.search(dto.expression)
            response.apply { resultCode = 200 }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Response().apply { resultCode = 500 }
        }
    }
}