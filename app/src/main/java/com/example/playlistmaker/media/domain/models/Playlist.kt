package com.example.playlistmaker.media.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String,
    val trackIds: List<Int>,
    val trackCount: Int
)