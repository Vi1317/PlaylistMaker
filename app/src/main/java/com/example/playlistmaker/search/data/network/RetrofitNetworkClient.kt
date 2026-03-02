package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val resp = iTunesService.search(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply {
                resultCode = resp.code()
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

    companion object {
        private val ITUNES_BASE_URL = "https://itunes.apple.com/"
    }
}