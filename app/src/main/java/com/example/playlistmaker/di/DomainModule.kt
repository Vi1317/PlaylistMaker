package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    //Поиск
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    //Плеер
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    //Настройки
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }
}