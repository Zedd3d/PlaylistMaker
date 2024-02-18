package com.zeddikus.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import com.zeddikus.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragmentViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<TrackRepositoryState>()
    private var prevFilter = ""
    private var searchJob: Job? = null

    private val onTrackClickDebounce =
        debounce<Track>(CLICK_DELAY, viewModelScope, false) { track ->
            showPlayer.postValue(track)
        }

    private val showPlayer = SingleLiveEvent<Track>()

    companion object {
        private const val CLICK_DELAY = 300L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun getState(): LiveData<TrackRepositoryState> = state

    fun getShowPlayerTrigger(): SingleLiveEvent<Track> = showPlayer

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        showHistory()
    }

    fun search(filter: String, locale: String) {

        if (filter.isNotEmpty()) {
            if (!(prevFilter == filter)) {
                prevFilter = filter

                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_DELAY)
                    state.postValue(TrackRepositoryState.SearchInProgress)
                    tracksInteractor
                        .searchTracks(filter, locale)
                        .collect { trackSearchResult ->
                            state.postValue(trackSearchResult.state)
                        }
                }
            }
        } else {
            showHistory()
        }

    }

    fun showHistory() {
        state.value = TrackRepositoryState.ShowHistory(searchHistoryInteractor.getHistory())
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }

    fun showPlayer(track: Track) {
        onTrackClickDebounce(track)
    }
}