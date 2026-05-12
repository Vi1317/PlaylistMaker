package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val track_id: Int,
    val track_name: String,
    val artist_name: String,
    val track_time_millis: Long,
    val artwork_url_100: String,
    val collection_name: String,
    val release_date: String?,
    val primary_genre_name: String,
    val country: String,
    val preview_url: String?,
    val addedAt: Long = System.currentTimeMillis()
)