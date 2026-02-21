package com.example.playlistmaker.domain

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.SearchHistoryManager
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.SettingsRepositoryImpl
import com.example.playlistmaker.data.dto.PlayerManager
import com.example.playlistmaker.data.dto.SettingsManager
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSettingsRepository(sharedPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(SettingsManager(sharedPreferences))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(PM_PREFERENCES, Context.MODE_PRIVATE)
        return SettingsInteractorImpl(getSettingsRepository(sharedPreferences))
    }

    private fun getSearchHistoryRepository(sharedPreferences: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SearchHistoryManager(sharedPreferences))
    }

    fun provideSearchTracksInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences = context.getSharedPreferences(PM_PREFERENCES, Context.MODE_PRIVATE)
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(sharedPreferences))
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(PlayerManager())
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    const val PM_PREFERENCES = "playlistmaker"
}