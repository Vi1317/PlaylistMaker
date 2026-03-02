package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: Result<List<Track>>)
    }
}