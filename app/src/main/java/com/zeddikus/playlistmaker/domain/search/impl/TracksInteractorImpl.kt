package com.zeddikus.playlistmaker.domain.search.impl

import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import kotlinx.coroutines.flow.Flow


class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, locale: String): Flow<TrackSearchResult> {
        return repository.searchTracks(expression, locale)
    }

}