package com.zeddikus.playlistmaker.domain.db

import com.zeddikus.playlistmaker.domain.sharing.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun favoriteTracks(): Flow<List<Track>>

    suspend fun switchFavoriteProperty(track: Track)

    suspend fun isFavorite(trackId: String): Boolean
}