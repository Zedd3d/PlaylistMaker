package com.zeddikus.playlistmaker.di

import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.data.converters.TrackConvertor
import com.zeddikus.playlistmaker.data.mediatec.playlists.impl.PlaylistsRepositoryImpl
import com.zeddikus.playlistmaker.data.player.impl.FavoritesRepositoryImpl
import com.zeddikus.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.zeddikus.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.zeddikus.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.zeddikus.playlistmaker.data.sharing.db.DefaultSettingsRepositoryImpl
import com.zeddikus.playlistmaker.domain.db.FavoritesRepository
import com.zeddikus.playlistmaker.domain.db.PlaylistsRepository
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.sharing.api.DefaultSettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val repositoryModule = module {
    factory<TracksRepository> {
        TracksRepositoryImpl(get(), TrackConvertor, get())
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
    }

    single<DefaultSettingsRepository> {
        DefaultSettingsRepositoryImpl(
            androidApplication().resources.getString(R.string.DEFAULT_EMAIL),
            androidApplication().resources.getString(R.string.thanks_template_subj),
            androidApplication().resources.getString(R.string.thanks_template_body),
            androidApplication().resources.getString(R.string.YP_LINK_AD),
            androidApplication().resources.getString(R.string.YP_LINK_OFFER),
        )
    }

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get())
    }

    factory<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get())
    }

}