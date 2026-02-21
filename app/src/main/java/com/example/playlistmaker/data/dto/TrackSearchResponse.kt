package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

data class TrackSearchResponse (val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response() {
}

fun TrackDto.toTrack(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate ?: "",
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}