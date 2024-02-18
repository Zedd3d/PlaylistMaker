package com.zeddikus.playlistmaker.domain.search.model

sealed interface TrackRepositoryState {
    object ErrorNetwork : TrackRepositoryState
    object ErrorEmpty : TrackRepositoryState
    data class ShowListResult(val trackList: List<Track> = emptyList<Track>()) :
        TrackRepositoryState

    data class ShowHistory(
        val trackList: List<Track> = emptyList<Track>(),
        val showAdapter: Boolean = true
    ) :
        TrackRepositoryState

    object SearchInProgress : TrackRepositoryState
}