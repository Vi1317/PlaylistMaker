package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.creator.ResourcesProviderImpl
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryStorage
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.ThemeStorage
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.ResourcesProvider
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    //Поиск
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext().getSharedPreferences("PLAYLIST_MAKER", Context.MODE_PRIVATE)
    }

    single {
        Gson()
    }

    single {
        SearchHistoryStorage(get(), get())
    }

    //Настройки
    single {
        ThemeStorage(get(), get())
    }

    single {
        ExternalNavigator(androidContext())
    }

    single<ResourcesProvider> {
        ResourcesProviderImpl(androidContext())
    }

    factory {
        MediaPlayer()
    }
}