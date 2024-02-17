package com.zeddikus.playlistmaker.domain.search.api

import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String, locale: String): Flow<TrackSearchResult>
}