package com.zeddikus.playlistmaker.data.sharing.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeddikus.playlistmaker.domain.settings.api.SharedPrefHandler
import com.zeddikus.playlistmaker.domain.settings.api.ThemeChanger

import com.zeddikus.playlistmaker.domain.sharing.model.Track


object SharedPreferencesImpl : SharedPrefHandler, ThemeChanger {

    val SP_DARK_THEME = "shared_preferences_dark_theme"
    val SP_SEARCH_HISTORY = "shared_preferences_search_history"
    private lateinit var sharedPref: SharedPreferences

    override fun setSharedPreferences(sharedPreferences: SharedPreferences) {
        sharedPref = sharedPreferences
        switchTheme(getCurrentTheme())
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    override fun setSharedPreferencesChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun clearHistory() {
        sharedPref.edit().remove(SP_SEARCH_HISTORY).apply()
    }

    override fun getHistory(): List<Track> {
        return Gson().fromJson<List<Track>>(
            sharedPref.getString(SP_SEARCH_HISTORY, "[]") ?: "[]",
            object : TypeToken<List<Track>>() {}.type
        )
    }

    @Synchronized
    override fun saveHistory(trackList: List<Track>) {
        val json = Gson().toJson(trackList)
        sharedPref.edit().putString(SP_SEARCH_HISTORY, json).apply()
    }

    override fun getCurrentTheme(): Boolean {
        return sharedPref.getBoolean(SP_DARK_THEME, false)
    }

    override fun saveSwitchDarkTheme(darkThemeEnabled: Boolean) {
        if (darkThemeEnabled) {
            sharedPref.edit().putBoolean(SP_DARK_THEME, true).apply()
        } else {
            sharedPref.edit().remove(SP_DARK_THEME).apply()
        }
        switchTheme(darkThemeEnabled)
    }

}