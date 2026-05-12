package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.FavoriteEntity
import com.example.playlistmaker.search.domain.Track

class FavoriteDbConvertor {

    fun map(track: Track): FavoriteEntity {
        return FavoriteEntity(track.trackId, track.trackName, track.artistName, track.trackTimeMillis,
            track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName,
            track.country, track.previewUrl, System.currentTimeMillis())
    }

    fun map(track: FavoriteEntity): Track {
        return Track(track.track_id, track.track_name, track.artist_name, track.track_time_millis,
            track.artwork_url_100, track.collection_name, track.release_date, track.primary_genre_name,
            track.country, track.preview_url)
    }
}