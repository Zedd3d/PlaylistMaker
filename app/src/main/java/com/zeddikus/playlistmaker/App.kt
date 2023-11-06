package com.zeddikus.playlistmaker

import android.app.Application
import com.zeddikus.playlistmaker.data.sharedpref.SharedPrefHandlerImpl

class App : Application() {

    companion object {
        var darkTheme = false
        val PLAYLIST_MAKER_SHARED_PREFERENCES = "playlist_maker_shared_preferences"
    }

    override fun onCreate() {
        super.onCreate()
        val spHandler = SharedPrefHandlerImpl
        spHandler.setSharedPreferences(getSharedPreferences(PLAYLIST_MAKER_SHARED_PREFERENCES, MODE_PRIVATE))
        darkTheme = spHandler.getCurrentTheme()
   }


}