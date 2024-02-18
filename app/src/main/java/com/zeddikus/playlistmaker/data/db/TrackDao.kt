package com.zeddikus.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY addTime DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getFavoritesTrackIDs(): List<String>

    @Query("SELECT * FROM track_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: String): List<TrackEntity>

    @RawQuery
    suspend fun trackInTable(rawQuery: SimpleSQLiteQuery): List<TrackEntity>
}