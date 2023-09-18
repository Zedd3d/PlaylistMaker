package com.zeddikus.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var darkTheme = false
    val PLAYLIST_MAKER_SHARED_PREFERENCES = "playlist_maker_shared_preferences"
    val SP_DARK_THEME = "shared_preferences_dark_theme"
    val SP_SEARCH_HISTORY = "shared_preferences_search_history"
    lateinit var sharedPref: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(PLAYLIST_MAKER_SHARED_PREFERENCES, MODE_PRIVATE)
        switchTheme(sharedPref.getBoolean(SP_DARK_THEME,false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun saveSwitchDarkTheme(darkThemeEnabled: Boolean){
        if (darkThemeEnabled) {
            sharedPref.edit().putBoolean(SP_DARK_THEME,true).apply()
        } else {
            sharedPref.edit().remove(SP_DARK_THEME).apply()
        }
        switchTheme(darkThemeEnabled)
    }


}