package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

sealed interface TrackInPlaylistState {
    data class AleradyIn(val playlistName: String = "") :
        TrackInPlaylistState

    data class AdditionSuccessful(val playlistName: String = "") :
        TrackInPlaylistState
}