package com.zeddikus.playlistmaker.domain.mediatec.favorites.impl

import com.zeddikus.playlistmaker.domain.db.FavoritesInteractor
import com.zeddikus.playlistmaker.domain.db.FavoritesRepository
import com.zeddikus.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override suspend fun favoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }

    override suspend fun switchFavoriteProperty(track: Track) {
        favoritesRepository.switchFavoriteProperty(track)
    }

    override suspend fun isFavorite(trackId: String): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }

}