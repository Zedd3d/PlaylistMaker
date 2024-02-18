package com.zeddikus.playlistmaker.domain.db

import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun switchFavoriteProperty(track: Track)

    suspend fun isFavorite(trackId: String): Boolean
    suspend fun getFavorites(): Flow<List<Track>>
    suspend fun getFavoritesId(): List<String>
    suspend fun trackInTable(trackId: String): Boolean
}