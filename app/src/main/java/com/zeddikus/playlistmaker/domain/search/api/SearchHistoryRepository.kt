package com.zeddikus.playlistmaker.domain.search.api

import com.zeddikus.playlistmaker.domain.sharing.model.Track

interface SearchHistoryRepository {
    fun clearHistory()

    fun getHistory(): List<Track>

    fun saveHistory(trackList: List<Track>)

    fun addTrackToHistory(track: Track)

    fun getSpSearchHistoryKey(): String

}