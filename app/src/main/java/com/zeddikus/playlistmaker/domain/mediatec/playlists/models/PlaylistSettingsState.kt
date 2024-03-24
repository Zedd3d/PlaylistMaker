package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

sealed interface PlaylistSettingsState {
    object NewPlaylist : PlaylistSettingsState
    object EditPlaylist : PlaylistSettingsState

    data class PlaylistData(val playlist: Playlist) : PlaylistSettingsState
}

