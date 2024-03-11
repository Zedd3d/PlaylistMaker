package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCover: String,
    val tracksCount: Int,
    val addTime: Long = 0
) : Parcelable