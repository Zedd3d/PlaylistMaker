package com.zeddikus.playlistmaker.di

import com.zeddikus.playlistmaker.data.settings.impl.ExternalNavigatorImpl
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.zeddikus.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.domain.settings.api.ExternalNavigator
import com.zeddikus.playlistmaker.domain.settings.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val interactorModule = module {
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    factory<MediaPlayer> {
        MediaPlayerInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidApplication())
    }


}