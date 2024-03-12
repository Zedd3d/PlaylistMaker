package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

import com.zeddikus.playlistmaker.domain.search.model.Track


data class PlaylistPropertys(
    val tracks: List<Track> = emptyList<Track>(),
    val playlist: Playlist,
    val playlistTime: String,
    val trackCount: String
)