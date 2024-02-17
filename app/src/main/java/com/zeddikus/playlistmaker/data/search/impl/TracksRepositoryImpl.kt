package com.zeddikus.playlistmaker.data.search.impl

import com.zeddikus.playlistmaker.data.converters.TrackConvertor
import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResponse
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackConvertor
) : TracksRepository {

    override fun searchTracks(expression: String, locale: String): Flow<TrackSearchResult> = flow {
        lateinit var state: TrackRepositoryState
        val response = networkClient.doRequest(TrackSearchRequest(expression, locale))
        if (response.resultCode == 200 && response is TrackSearchResponse) {
            val listTracks = trackMapper.map(response.results)

            state = if (listTracks.isEmpty()) {
                TrackRepositoryState.ErrorEmpty
            } else {
                TrackRepositoryState.ShowListResult(listTracks)
            }

            emit(TrackSearchResult(state))
        } else if (response.resultCode == 400) {
            emit(TrackSearchResult(TrackRepositoryState.ErrorNetwork))
        } else {
            emit(TrackSearchResult(TrackRepositoryState.ErrorNetwork))
        }
    }
}