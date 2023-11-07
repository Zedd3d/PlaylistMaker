package com.zeddikus.playlistmaker.domain.api

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.zeddikus.playlistmaker.domain.models.Track

interface SharedPrefHandler {

    fun setSharedPreferences(sharedPreferences: SharedPreferences)

    fun setSharedPreferencesChangeListener(listener: OnSharedPreferenceChangeListener)

    fun clearHistory()

    fun getHistory(): List<Track>

    fun saveHistory(trackList: List<Track>)
}