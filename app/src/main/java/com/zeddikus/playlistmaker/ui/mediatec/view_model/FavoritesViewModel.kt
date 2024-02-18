package com.zeddikus.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.FavoritesInteractor
import com.zeddikus.playlistmaker.domain.mediatec.favorites.models.FavoritesState
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import com.zeddikus.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val state = MutableLiveData<FavoritesState>()

    private val onTrackClickDebounce =
        debounce<Track>(CLICK_DELAY, viewModelScope, false) { track ->
            showPlayer.postValue(track)
        }

    private val showPlayer = SingleLiveEvent<Track>()

    companion object {
        private const val CLICK_DELAY = 300L
    }

    init {
        fillData()
    }

    fun getShowPlayerTrigger(): SingleLiveEvent<Track> = showPlayer

    fun getState(): LiveData<FavoritesState> = state

    fun fillData() {
        state.postValue(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor
                .favoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        state.postValue(FavoritesState.Empty)
                    } else {
                        state.postValue(FavoritesState.Content(tracks))
                    }
                }
        }
    }

    fun showPlayer(track: Track) {
        onTrackClickDebounce(track)
    }
}