package com.zeddikus.playlistmaker.data.sharedpref

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.zeddikus.playlistmaker.Creator
import com.zeddikus.playlistmaker.domain.api.SharedPrefHandler
import com.zeddikus.playlistmaker.domain.api.ThemeChanger
import com.zeddikus.playlistmaker.domain.api.genericType
import com.zeddikus.playlistmaker.domain.models.Track


object SharedPrefHandlerImpl : SharedPrefHandler, ThemeChanger {

    val SP_DARK_THEME = "shared_preferences_dark_theme"
    val SP_SEARCH_HISTORY = "shared_preferences_search_history"
    private lateinit var sharedPref:SharedPreferences
    private var jsonHandler = Creator.provideJsonHandler()

    override fun setSharedPreferences(sharedPreferences: SharedPreferences){
        sharedPref = sharedPreferences
        switchTheme(getCurrentTheme())
    }


    override fun setSharedPreferencesChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    override fun getCurrentTheme(): Boolean {
        return sharedPref.getBoolean(SP_DARK_THEME,false)
    }

    override fun saveSwitchDarkTheme(darkThemeEnabled: Boolean){
        if (darkThemeEnabled) {
            sharedPref.edit().putBoolean(SP_DARK_THEME,true).apply()
        } else {
            sharedPref.edit().remove(SP_DARK_THEME).apply()
        }
        switchTheme(darkThemeEnabled)
    }

    override fun clearHistory() {
        sharedPref.edit().remove(SP_SEARCH_HISTORY).apply()
    }

    override fun getHistory(): List<Track> {
        return jsonHandler.fromJson<List<Track>>(sharedPref.getString(SP_SEARCH_HISTORY,"[]")?: "[]", jsonHandler.genericType<List<Track>>())
    }

    @Synchronized
    override fun saveHistory(trackList: List<Track>) {
        val json = jsonHandler.toJson(trackList)
        sharedPref.edit().putString(SP_SEARCH_HISTORY,json).apply()
    }



}