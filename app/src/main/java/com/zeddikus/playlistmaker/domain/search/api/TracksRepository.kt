package com.zeddikus.playlistmaker.domain.search.api

import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult

interface TracksRepository {
    fun searchTracks(expression: String, locale: String): TrackSearchResult
}