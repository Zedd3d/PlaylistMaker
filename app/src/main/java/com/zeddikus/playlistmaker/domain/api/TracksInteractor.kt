package com.zeddikus.playlistmaker.domain.api

import com.zeddikus.playlistmaker.data.dto.TrackSearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, locale: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(trackSearchResult: TrackSearchResult)
    }
}