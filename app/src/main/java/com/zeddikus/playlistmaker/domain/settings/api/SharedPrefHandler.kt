package com.zeddikus.playlistmaker.domain.settings.api

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.zeddikus.playlistmaker.domain.sharing.model.Track

interface SharedPrefHandler {


    fun setSharedPreferencesChangeListener(listener: OnSharedPreferenceChangeListener)

    fun clearHistory()

    fun getHistory(): List<Track>

    fun saveHistory(trackList: List<Track>)
}