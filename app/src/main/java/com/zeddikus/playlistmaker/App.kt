package com.zeddikus.playlistmaker

import android.app.Application
import com.zeddikus.playlistmaker.creator.Creator
import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl

class App : Application() {

    companion object {
        val PLAYLIST_MAKER_SHARED_PREFERENCES = "playlist_maker_shared_preferences"
    }

    override fun onCreate() {
        super.onCreate()
        val spHandler = SharedPreferencesImpl
        spHandler.setSharedPreferences(
            getSharedPreferences(
                PLAYLIST_MAKER_SHARED_PREFERENCES,
                MODE_PRIVATE
            )
        )
        Creator.setApplication(this)
    }

}