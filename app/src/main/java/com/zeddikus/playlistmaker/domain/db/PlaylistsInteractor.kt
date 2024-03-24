package com.zeddikus.playlistmaker.domain.db

import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylistById(playlistId: Int)

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun addToPlayList(playlist: Playlist, track: Track)

    suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track)
    suspend fun trackIsAlreadyOnThePlaylist(playlist: Playlist, track: Track): Boolean

    suspend fun tracksInPlaylist(playlist: Playlist): Flow<List<Track>>

    suspend fun getPlaylistById(playlistID: Int): Playlist

}