package com.zeddikus.playlistmaker.domain.db

import com.zeddikus.playlistmaker.domain.sharing.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun switchFavoriteProperty(track: Track)

    suspend fun isFavorite(trackId: String): Boolean
    suspend fun getFavorites(): Flow<List<Track>>

    suspend fun trackInTable(trackId: String): Boolean
}