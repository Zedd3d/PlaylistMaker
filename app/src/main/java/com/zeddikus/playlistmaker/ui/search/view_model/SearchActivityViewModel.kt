package com.zeddikus.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeddikus.playlistmaker.creator.Creator
import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class SearchActivityViewModel : ViewModel() {
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private val state = MutableLiveData<TrackRepositoryState>()

    fun getState(): LiveData<TrackRepositoryState> = state

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        showHistory()
    }

    fun search(filter: String, locale: String) {
        state.value = TrackRepositoryState.searchInProgress

        if (filter.isNotEmpty()) {
            tracksInteractor.searchTracks(filter, locale,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(trackSearchResult: TrackSearchResult) {
                        state.postValue(trackSearchResult.state)
                    }
                })
        } else {
            showHistory()
        }
    }

    fun showHistory() {
        state.value = TrackRepositoryState.showHistory(searchHistoryInteractor.getHistory())
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }
}