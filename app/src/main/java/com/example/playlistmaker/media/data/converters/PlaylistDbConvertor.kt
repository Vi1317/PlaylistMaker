package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(playlist.id, playlist.name, playlist.description, playlist.coverPath, gson.toJson(playlist.trackIds), playlist.trackCount)
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        val trackIds: List<Int> = gson.fromJson(playlist.trackIds, type) ?: emptyList()
        return Playlist(playlist.id, playlist.name, playlist.description, playlist.coverPath, trackIds, playlist.trackCount)
    }
}