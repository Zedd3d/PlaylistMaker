package com.zeddikus.playlistmaker.di

import android.app.Application
import com.google.gson.Gson
import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl
import com.zeddikus.playlistmaker.domain.settings.api.SharedPrefHandler
import com.zeddikus.playlistmaker.domain.settings.api.ThemeChanger
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val sharedPreferencesModule = module {

    single<ThemeChanger> {
        SharedPreferencesImpl(
            get(),
            androidApplication().getSharedPreferences(
                "playlist_maker_shared_preferences",
                Application.MODE_PRIVATE
            )
        )
    }
    single<SharedPrefHandler> {
        SharedPreferencesImpl(
            get(),
            androidApplication().getSharedPreferences(
                "playlist_maker_shared_preferences",
                Application.MODE_PRIVATE
            )
        )
    }

    factory { Gson() }

}