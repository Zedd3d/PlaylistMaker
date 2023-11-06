package com.zeddikus.playlistmaker.domain.api

import com.zeddikus.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun clearHistory()

    fun getHistory(): List<Track>

    fun saveHistory(trackList: List<Track>)

    fun addTrackToHistory(track: Track)

    fun getSpSearchHistoryKey(): String

}