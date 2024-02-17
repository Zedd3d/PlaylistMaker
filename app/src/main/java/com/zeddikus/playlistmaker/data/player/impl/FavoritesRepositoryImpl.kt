package com.zeddikus.playlistmaker.data.player.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.zeddikus.playlistmaker.data.converters.TrackConvertor
import com.zeddikus.playlistmaker.data.converters.TrackDbConvertor
import com.zeddikus.playlistmaker.data.db.AppDatabase
import com.zeddikus.playlistmaker.data.db.TrackEntity
import com.zeddikus.playlistmaker.domain.db.FavoritesRepository
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
) : FavoritesRepository {

    override suspend fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> TrackConvertor.map(TrackDbConvertor.map(track)) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return TrackDbConvertor.map(TrackConvertor.map(track))
    }

    override suspend fun switchFavoriteProperty(track: Track) {
        val trackEntity = convertToTrackEntity(track)
        if (!isFavorite(track.trackId)) {
            trackEntity.addTime = System.currentTimeMillis()
            appDatabase.trackDao().insertTrack(trackEntity)
        } else {
            appDatabase.trackDao().deleteTrack(trackEntity)
        }
    }

    override suspend fun isFavorite(trackId: String): Boolean {
        return appDatabase.trackDao().getTrackById(trackId).isNotEmpty()
    }

    override suspend fun trackInTable(trackId: String): Boolean {
        //Тут просто поупражнялся с произвольным запросом. Оставлю для себя в качестве примера
        return appDatabase.trackDao().trackInTable(
            SimpleSQLiteQuery(
                "SELECT * FROM track_table WHERE trackId = ?",
                arrayOf<Any>(trackId)
            )
        ).isNotEmpty()
    }

}