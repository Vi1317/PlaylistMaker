package com.example.playlistmaker.di

import com.example.playlistmaker.player.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.viewmodel.SettingsViewModel

val viewModelModule = module {
    //Поиск
    viewModel {
        SearchViewModel(get(), get())
    }

    //Плеер
    viewModel { (track: Track) ->
        PlayerViewModel(get(), track)
    }

    //Настройки
    viewModel {
        SettingsViewModel(get(), get())
    }
}