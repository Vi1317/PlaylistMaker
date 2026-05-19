package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteDao
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.entity.FavoriteEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity

@Database(version = 2, entities = [FavoriteEntity::class, PlaylistEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        fun getInstance(context: android.content.Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "playlistmaker.db"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}