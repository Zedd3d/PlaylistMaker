package com.zeddikus.playlistmaker.data.impl

import com.zeddikus.playlistmaker.domain.models.TrackRepositoryState
import com.zeddikus.playlistmaker.data.NetworkClient
import com.zeddikus.playlistmaker.data.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.dto.TrackSearchResponse
import com.zeddikus.playlistmaker.domain.api.TracksRepository
import com.zeddikus.playlistmaker.domain.models.Track
import com.zeddikus.playlistmaker.data.dto.TrackSearchResult
import java.text.SimpleDateFormat
import java.util.Locale


class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String, locale: String): TrackSearchResult {
        var state = TrackRepositoryState.SEARCH_IN_PROGRESS
        val response = networkClient.doRequest(TrackSearchRequest(expression, locale))
        if (response.resultCode == 200) {
            val listTracks = (response as TrackSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.trackId,
                    it.primaryGenreName,
                    it.collectionName,
                    it.releaseDate,
                    it.country,
                    it.previewUrl
                )
            }

            if (listTracks.isEmpty()) {
                state = TrackRepositoryState.ERROR_EMPTY
            } else {
                state = TrackRepositoryState.OK
            }

            return TrackSearchResult(state,listTracks)
        } else if (response.resultCode == 400){
            return TrackSearchResult(TrackRepositoryState.ERROR_NETWORK,emptyList())
        } else {
            return TrackSearchResult(TrackRepositoryState.ERROR_NETWORK,emptyList())
        }
    }
}