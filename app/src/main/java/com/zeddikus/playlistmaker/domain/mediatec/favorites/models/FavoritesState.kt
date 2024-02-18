package com.zeddikus.playlistmaker.domain.mediatec.favorites.models

import com.zeddikus.playlistmaker.domain.search.model.Track

sealed interface FavoritesState {

    object Loading : FavoritesState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesState

    object Empty : FavoritesState
}