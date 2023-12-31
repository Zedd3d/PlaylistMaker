package com.zeddikus.playlistmaker.domain.search.api

import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, locale: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(trackSearchResult: TrackSearchResult)
    }
}