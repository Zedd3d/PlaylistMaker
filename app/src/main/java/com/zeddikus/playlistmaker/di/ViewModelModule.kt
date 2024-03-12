package com.zeddikus.playlistmaker.di

import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.mediatec.view_model.FavoritesViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.MediatecViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistSettingsViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistViewModel
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import com.zeddikus.playlistmaker.ui.player.view_model.PlayerViewModel
import com.zeddikus.playlistmaker.ui.search.view_model.SearchFragmentViewModel
import com.zeddikus.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchFragmentViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        MediatecViewModel()
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel { (playlistId: Int?) ->
        PlaylistSettingsViewModel(playlistId, get())
    }

    viewModel { (playlistId: Int) ->
        PlaylistViewModel(playlistId, get(), get())
    }

}