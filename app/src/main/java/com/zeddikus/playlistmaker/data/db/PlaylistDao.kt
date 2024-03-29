package com.zeddikus.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlayList(trackInPlaylist: TrackInPlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_list_table WHERE playlistId = :playlistId AND trackID = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: String)

    @Query("SELECT * FROM playlist_list_table WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun trackIsAlreadyOnThePlaylist(
        playlistId: Int,
        trackId: String
    ): List<TrackInPlaylistEntity>

    @Query("SELECT * FROM playlist_list_table WHERE playlistId = :playlistId ORDER BY addTime DESC")
    suspend fun tracksInPlaylist(
        playlistId: Int
    ): List<TrackInPlaylistEntity>

    @RawQuery
    suspend fun getPlayLists(rawQuery: SimpleSQLiteQuery): List<PlaylistEntity>

    @RawQuery
    suspend fun getPlayListById(rawQuery: SimpleSQLiteQuery): PlaylistEntity

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