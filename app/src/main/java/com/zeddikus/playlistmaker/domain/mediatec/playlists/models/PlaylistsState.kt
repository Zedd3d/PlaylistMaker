package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

sealed interface PlaylistsState {
    data class ShowListResult(val playlistsList: List<Playlist> = emptyList<Playlist>()) :
        PlaylistsState

    object Empty : PlaylistsState
}