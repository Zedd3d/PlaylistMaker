package com.zeddikus.playlistmaker.domain.db

import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylistById(playlistID: Int)
    suspend fun addToPlayList(playlist: Playlist, track: Track)

    suspend fun trackIsAlreadyOnThePlaylist(playlist: Playlist, track: Track): Boolean
}