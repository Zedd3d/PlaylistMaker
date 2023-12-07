package com.zeddikus.playlistmaker.domain.search.model

import com.zeddikus.playlistmaker.domain.sharing.model.Track

sealed interface TrackRepositoryState {
    object errorNetwork : TrackRepositoryState
    object errorEmpty : TrackRepositoryState
    data class showListResult(val trackList: List<Track> = emptyList<Track>()) :
        TrackRepositoryState

    data class showHistory(
        val trackList: List<Track> = emptyList<Track>(),
        val showAdapter: Boolean = true
    ) :
        TrackRepositoryState

    object searchInProgress : TrackRepositoryState
}