package com.zeddikus.playlistmaker.data.search.impl

import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResponse
import com.zeddikus.playlistmaker.data.search.mapper.TrackMapper
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String, locale: String): TrackSearchResult {
        lateinit var state: TrackRepositoryState
        val response = networkClient.doRequest(TrackSearchRequest(expression, locale))
        if (response.resultCode == 200 && response is TrackSearchResponse) {
            val listTracks = TrackMapper.map(response.results)

            if (listTracks.isEmpty()) {
                state = TrackRepositoryState.errorEmpty
            } else {
                state = TrackRepositoryState.showListResult(listTracks)
            }

            return TrackSearchResult(state, listTracks)
        } else if (response.resultCode == 400) {
            return TrackSearchResult(TrackRepositoryState.errorNetwork, emptyList())
        } else {
            return TrackSearchResult(TrackRepositoryState.errorNetwork, emptyList())
        }
    }
}