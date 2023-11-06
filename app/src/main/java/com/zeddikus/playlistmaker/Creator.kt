package com.zeddikus.playlistmaker

import com.zeddikus.playlistmaker.data.impl.JsonHandlerRepositoryImpl
import com.zeddikus.playlistmaker.data.impl.MediaPlayerRepositoryImpl
import com.zeddikus.playlistmaker.data.impl.SearchHistoryRepositoryImpl
import com.zeddikus.playlistmaker.data.impl.TracksRepositoryImpl
import com.zeddikus.playlistmaker.data.network.RetrofitNetworkClient
import com.zeddikus.playlistmaker.data.sharedpref.SharedPrefHandlerImpl
import com.zeddikus.playlistmaker.domain.api.JsonHandlerInteractor
import com.zeddikus.playlistmaker.domain.api.JsonHandlerRepository
import com.zeddikus.playlistmaker.domain.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.api.SharedPrefHandler
import com.zeddikus.playlistmaker.domain.api.ThemeChanger
import com.zeddikus.playlistmaker.domain.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.api.TracksRepository
import com.zeddikus.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.zeddikus.playlistmaker.domain.impl.JsonHandlerInteractorInteractorImpl
import com.zeddikus.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.zeddikus.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository{
        return MediaPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): MediaPlayer {
        return AudioPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl()
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getJsonHandlerRepository(): JsonHandlerRepository {
        return JsonHandlerRepositoryImpl()
    }

    fun provideJsonHandler(): JsonHandlerInteractor {
        return JsonHandlerInteractorInteractorImpl(getJsonHandlerRepository())
    }

    fun provideSharedPreferencesHandler(): SharedPrefHandler {
        return SharedPrefHandlerImpl
    }

    fun provideThemeChanger(): ThemeChanger {
        return SharedPrefHandlerImpl
    }
}