package com.zeddikus.playlistmaker.data.search.impl

import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResponse
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchResult
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String, locale: String): TrackSearchResult {
        lateinit var state: TrackRepositoryState
        val response = networkClient.doRequest(TrackSearchRequest(expression, locale))
        if (response.resultCode == 200) {
            val listTracks = (response as TrackSearchResponse).results.map {
                Track(
                    it.trackName?: "",
                    it.artistName?: "",
                    it.trackTimeMillis?: 0L,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)?: "00:00",
                    it.artworkUrl100?: "",
                    it.trackId?: "0",
                    it.primaryGenreName?: "",
                    it.collectionName?: "",
                    it.releaseDate?: "0001-01-01T00:00:00Z",
                    it.country?: "",
                    it.previewUrl?: ""
                )
            }

            if (listTracks.isEmpty()) {
                state = TrackRepositoryState.errorEmpty
            } else {
                state = TrackRepositoryState.showListResult(listTracks)
            }

            return TrackSearchResult(state,listTracks)
        } else if (response.resultCode == 400){
            return TrackSearchResult(TrackRepositoryState.errorNetwork, emptyList())
        } else {
            return TrackSearchResult(TrackRepositoryState.errorNetwork, emptyList())
        }
    }
}