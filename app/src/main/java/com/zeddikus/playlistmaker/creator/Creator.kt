package com.zeddikus.playlistmaker.creator


import android.app.Application
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.zeddikus.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.zeddikus.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.zeddikus.playlistmaker.data.settings.impl.ExternalNavigatorImpl
import com.zeddikus.playlistmaker.data.sharing.db.DefaultSettingsRepositoryImpl
import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl
import com.zeddikus.playlistmaker.data.sharing.network.RetrofitNetworkClient
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.zeddikus.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.domain.settings.api.ExternalNavigator
import com.zeddikus.playlistmaker.domain.settings.api.ThemeChanger
import com.zeddikus.playlistmaker.domain.settings.impl.SharingInteractorImpl

object Creator {


    private lateinit var application: Application

    fun setApplication(application: Application) {
        this.application = application

        DefaultSettingsRepositoryImpl.setDefaultValues(
            this.application.resources.getString(R.string.DEFAULT_EMAIL),
            this.application.resources.getString(R.string.thanks_template_subj),
            this.application.resources.getString(R.string.thanks_template_body),
            this.application.resources.getString(R.string.YP_LINK_AD),
            this.application.resources.getString(R.string.YP_LINK_OFFER),
        )
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): MediaPlayer {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl()
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    fun provideThemeChanger(): ThemeChanger {
        return SharedPreferencesImpl
    }

    fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), provideDefaultSettingsRepository())
    }

    fun provideDefaultSettingsRepository(): DefaultSettingsRepositoryImpl {
        return DefaultSettingsRepositoryImpl
    }
}