package com.zeddikus.playlistmaker.domain.search.api

import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResult

interface TracksRepository {
    fun searchTracks(expression: String, locale: String): TrackSearchResult
}