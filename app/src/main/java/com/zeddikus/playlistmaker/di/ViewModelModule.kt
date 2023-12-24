package com.zeddikus.playlistmaker.di

import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.mediatec.view_model.FavoritesViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.MediatecViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import com.zeddikus.playlistmaker.ui.player.view_model.PlayerViewModel
import com.zeddikus.playlistmaker.ui.search.view_model.SearchActivityViewModel
import com.zeddikus.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchActivityViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        MediatecViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}