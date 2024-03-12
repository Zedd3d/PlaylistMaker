package com.zeddikus.playlistmaker.data.mediatec.playlists.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.zeddikus.playlistmaker.data.converters.PlaylistDbConvertor
import com.zeddikus.playlistmaker.data.converters.TrackConvertor
import com.zeddikus.playlistmaker.data.db.AppDatabase
import com.zeddikus.playlistmaker.data.db.PlaylistEntity
import com.zeddikus.playlistmaker.data.db.TrackInPlaylistEntity
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
                    "\n,CASE WHEN t1.allTime is null THEN 0" +
                    "\n     ELSE t1.allTime " +
                    "\nEND as playlistTimeMillis" +
                    "\nFROM playlist_table AS playlists" +
                    "\nLEFT OUTER JOIN (SELECT " +
                    "\n                   t.playlistID" +
                    "\n                   ,SUM(1) as count" +
                    "\n                   ,SUM(t.trackTimeMillis) as allTime" +
                    "\n                    FROM playlist_list_table AS t" +
                    "\n                    GROUP BY t.playlistID" +
                    "\n) AS t1 " +
                    "\nON t1.playlistID = playlists.playlistID" +
                    "\nORDER BY addTime DESC",
        )
        val playlists = appDatabase.playlistDao().getPlayLists(query)
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun getPlaylistById(playlistID: Int): Playlist {
        val query = SimpleSQLiteQuery(
            "SELECT " +
                    "\n * " +
                    "\n,CASE WHEN t1.count is null THEN 0" +
                    "\n     ELSE t1.count " +
                    "\nEND as tracksCount" +
                    "\n,CASE WHEN t1.allTime is null THEN 0" +
                    "\n     ELSE t1.allTime " +
                    "\nEND as playlistTimeMillis" +
                    "\nFROM playlist_table AS playlists" +
                    "\nLEFT OUTER JOIN (SELECT " +
                    "\n                   t.playlistID" +
                    "\n                   ,SUM(1) as count" +
                    "\n                   ,SUM(t.trackTimeMillis) as allTime" +
                    "\n                    FROM playlist_list_table AS t" +
                    "\n                    GROUP BY t.playlistID" +
                    "\n) AS t1 " +
                    "\nON t1.playlistID = playlists.playlistID" +
                    "\nWHERE playlists.playlistID = ?" +
                    "\nORDER BY addTime DESC",
            arrayOf<Any>(playlistID)
        )
        val playlist = appDatabase.playlistDao().getPlayListById(query)
        return PlaylistDbConvertor.mapToPlayList(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistDbConvertor.mapToPlaylistEntity(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> PlaylistDbConvertor.mapToPlayList(playlistEntity) }
    }

    private fun convertFromTrackInPlaylistEntity(trackInPlaylistEntity: List<TrackInPlaylistEntity>): List<Track> {
        return trackInPlaylistEntity.map { trackInPlaylistEntity ->
            TrackConvertor.map(trackInPlaylistEntity)
        }
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

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, track: Track) {
        appDatabase.playlistDao().deleteTrackFromPlaylist(playlist.playlistId, track.trackId)
    }

    override suspend fun trackIsAlreadyOnThePlaylist(playlist: Playlist, track: Track): Boolean {
        return appDatabase.playlistDao()
            .trackIsAlreadyOnThePlaylist(playlist.playlistId, track.trackId).isNotEmpty()
    }

    override suspend fun tracksInPlaylist(playlist: Playlist): Flow<List<Track>> = flow {
        val result = appDatabase.playlistDao()
            .tracksInPlaylist(playlist.playlistId)
        emit(convertFromTrackInPlaylistEntity(result))
    }
}