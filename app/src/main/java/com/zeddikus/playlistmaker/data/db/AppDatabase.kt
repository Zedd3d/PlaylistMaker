package com.zeddikus.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao
}