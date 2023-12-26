package com.zeddikus.playlistmaker

import android.app.Application
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
        (getKoin().get() as ThemeChanger)
    }
}