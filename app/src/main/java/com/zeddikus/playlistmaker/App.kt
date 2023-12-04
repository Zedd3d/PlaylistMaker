package com.zeddikus.playlistmaker

import android.app.Application
import com.zeddikus.playlistmaker.creator.Creator
import com.zeddikus.playlistmaker.data.sharing.db.DefaultSettingsRepository
import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl

class App : Application() {

    companion object {
        var darkTheme = false
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
        darkTheme = spHandler.getCurrentTheme()
        DefaultSettingsRepository.setDefaultValues(
            resources.getString(R.string.DEFAULT_EMAIL),
            resources.getString(R.string.thanks_template_subj),
            resources.getString(R.string.thanks_template_body),
            resources.getString(R.string.YP_LINK_AD),
            resources.getString(R.string.YP_LINK_OFFER),
        )

        Creator.setApplication(this)
    }

}