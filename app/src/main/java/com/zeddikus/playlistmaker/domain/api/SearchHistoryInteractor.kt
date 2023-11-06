package com.zeddikus.playlistmaker.domain.api

import com.zeddikus.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    val SP_SEARCH_HISTORY: String

    fun clearHistory()

    fun getHistory(): List<Track>

    fun saveHistory(trackList: List<Track>)

    fun addTrackToHistory(track: Track)

}