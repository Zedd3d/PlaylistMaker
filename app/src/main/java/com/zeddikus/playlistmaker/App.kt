package com.zeddikus.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl
import com.zeddikus.playlistmaker.di.dataModule
import com.zeddikus.playlistmaker.di.interactorModule
import com.zeddikus.playlistmaker.di.repositoryModule
import com.zeddikus.playlistmaker.di.sharedPreferencesModule
import com.zeddikus.playlistmaker.di.viewModelModule
import com.zeddikus.playlistmaker.domain.settings.api.ThemeChanger
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(
                dataModule,
                interactorModule,
                repositoryModule,
                sharedPreferencesModule,
                viewModelModule
            )
        }

        val themeChanger = (getKoin().get() as ThemeChanger)
        val themeInitalise = getSharedPreferences(
            "playlist_maker_shared_preferences",
            MODE_PRIVATE
        ).contains(SharedPreferencesImpl.SP_DARK_THEME)
        if (!themeInitalise) {
            val currentNightMode: Int =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> themeChanger.saveSwitchDarkTheme(false)
                Configuration.UI_MODE_NIGHT_YES -> themeChanger.saveSwitchDarkTheme(true)
            }
        }
    }
}