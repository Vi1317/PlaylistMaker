package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(track: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(track: FavoriteEntity)

    @Query("SELECT * FROM favorite_table ORDER BY addedAt DESC")
    fun getFavorite(): Flow<List<FavoriteEntity>>

    @Query("SELECT track_id FROM favorite_table")
    suspend fun getFavoriteIds(): List<Int>
}