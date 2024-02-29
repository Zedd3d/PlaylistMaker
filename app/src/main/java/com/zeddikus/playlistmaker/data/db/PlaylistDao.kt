package com.zeddikus.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlayList(trackInPlaylist: TrackInPlaylistEntity)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity

    @Query("SELECT * FROM playlist_list_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun trackIsAlreadyOnThePlaylist(
        playlistId: Int,
        trackId: String
    ): List<TrackInPlaylistEntity>

    @RawQuery
    suspend fun getPlayLists(rawQuery: SimpleSQLiteQuery): List<PlaylistEntity>

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deleteSinglePlayListById(playlistId: Int)

    @Query("DELETE FROM playlist_list_table WHERE playlistId = :playlistId")
    suspend fun deleteTracksListByPlaylistId(playlistId: Int)

    @Transaction
    suspend fun deletePlayListById(playlistId: Int) {
        //Сделал посмотреть как работают транзакции
        deleteSinglePlayListById(playlistId)
        deleteTracksListByPlaylistId(playlistId)
    }

}