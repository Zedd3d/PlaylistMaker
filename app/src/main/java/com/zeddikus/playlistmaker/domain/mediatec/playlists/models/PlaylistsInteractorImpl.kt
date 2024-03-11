package com.zeddikus.playlistmaker.domain.mediatec.playlists.models

import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.db.PlaylistsRepository
import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {
    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun deletePlaylistById(playlistId: Int) {
        playlistsRepository.deletePlaylistById(playlistId)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistsRepository.insertPlaylist(playlist)
    }

    override suspend fun addToPlayList(playlist: Playlist, track: Track) {
        playlistsRepository.addToPlayList(playlist, track)
    }

    override suspend fun trackIsAlreadyOnThePlaylist(playlist: Playlist, track: Track): Boolean {
        return playlistsRepository.trackIsAlreadyOnThePlaylist(playlist, track)
    }

}