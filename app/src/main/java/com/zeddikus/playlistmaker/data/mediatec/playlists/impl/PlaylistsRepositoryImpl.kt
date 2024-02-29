package com.zeddikus.playlistmaker.data.mediatec.playlists.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.zeddikus.playlistmaker.data.converters.PlaylistDbConvertor
import com.zeddikus.playlistmaker.data.db.AppDatabase
import com.zeddikus.playlistmaker.data.db.PlaylistEntity
import com.zeddikus.playlistmaker.domain.db.PlaylistsRepository
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
) : PlaylistsRepository {

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val query = SimpleSQLiteQuery(
            "SELECT " +
                    "\n * " +
                    "\n,CASE WHEN t1.count is null THEN 0" +
                    "\n     ELSE t1.count " +
                    "\nEND as tracksCount" +
                    "\nFROM playlist_table AS playlists" +
                    "\nLEFT OUTER JOIN (SELECT " +
                    "\n                   t.playlistID" +
                    "\n                   ,SUM(1) as count" +
                    "\n                    FROM playlist_list_table AS t" +
                    "\n                    GROUP BY t.playlistID" +
                    "\n) AS t1 " +
                    "\nON t1.playlistID = playlists.playlistID" +
                    "\nORDER BY addTime DESC",
        )
        val playlists = appDatabase.playlistDao().getPlayLists(query)
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> PlaylistDbConvertor.mapToPlayList(playlistEntity) }
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistDbConvertor.mapToPlaylistEntity(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylistById(playlistID: Int) {
        appDatabase.playlistDao().deletePlayListById(playlistID)
    }

    override suspend fun addToPlayList(playlist: Playlist, track: Track) {
        appDatabase.playlistDao().addTrackToPlayList(
            PlaylistDbConvertor.mapToTrackInPlaylistEntity(playlist, track)
        )
    }

    override suspend fun trackIsAlreadyOnThePlaylist(playlist: Playlist, track: Track): Boolean {
        return appDatabase.playlistDao()
            .trackIsAlreadyOnThePlaylist(playlist.playlistId, track.trackId).isNotEmpty()
    }
}