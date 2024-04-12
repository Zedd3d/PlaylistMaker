package com.zeddikus.playlistmaker.data.search.impl

import com.zeddikus.playlistmaker.data.converters.TrackConvertor
import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResponse
import com.zeddikus.playlistmaker.domain.db.FavoritesRepository
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackConvertor,
    private val favoritesRepository: FavoritesRepository
) : TracksRepository {

    override fun searchTracks(expression: String, locale: String): Flow<TrackSearchResult> = flow {
        lateinit var state: TrackRepositoryState
        val listFavoritesID = favoritesRepository.getFavoritesId()
        val response = networkClient.doRequest(TrackSearchRequest(expression, locale))
        if (response.resultCode == 200 && response is TrackSearchResponse) {
            val listTracks = trackMapper.map(response.results)

            listTracks.forEach { track: Track ->
                track.isFavorite = listFavoritesID.contains(track.trackId)
            }
            state = if (listTracks.isEmpty()) {
                TrackRepositoryState.ErrorEmpty
            } else {
                TrackRepositoryState.Content(listVacancies)
            }

            emit(TrackSearchResult(state))
        } else if (response.resultCode == 400) {
            emit(TrackSearchResult(TrackRepositoryState.ErrorNetwork))
        } else {
            emit(TrackSearchResult(TrackRepositoryState.ErrorNetwork))
        }
    }
}