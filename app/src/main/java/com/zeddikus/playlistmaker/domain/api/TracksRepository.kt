package com.zeddikus.playlistmaker.domain.api

import com.zeddikus.playlistmaker.data.dto.TrackSearchResult

interface TracksRepository {
    fun searchTracks(expression: String, locale: String): TrackSearchResult
}