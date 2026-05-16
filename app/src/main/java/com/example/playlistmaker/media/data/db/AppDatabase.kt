package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavoriteDao
import com.example.playlistmaker.media.data.db.entity.FavoriteEntity

@Database(version = 1, entities = [FavoriteEntity::class])
abstract class AppDatabase : RoomDatabase(){

    abstract fun favoriteDao(): FavoriteDao

}