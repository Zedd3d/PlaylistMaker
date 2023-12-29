package com.zeddikus.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.search.model.TrackSearchResult
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.SingleLiveEvent

class SearchFragmentViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val state = MutableLiveData<TrackRepositoryState>()
    private var isNowPausingBetweenClicks = false

    //private val showPlayer = MutableLiveData<Track>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var prevFilter = ""

    companion object {
        private const val CLICK_DELAY = 1500L
    }

    fun getState(): LiveData<TrackRepositoryState> = state

    //fun getshowPlayerTrigger(): LiveData<Track> = showPlayer

    private val showPlayer = SingleLiveEvent<Track>()
    fun getshowPlayerTrigger(): SingleLiveEvent<Track> = showPlayer

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        showHistory()
    }

    fun search(filter: String, locale: String) {

        if (filter.isNotEmpty()) {
            if (!(prevFilter == filter)) {
                state.value = TrackRepositoryState.searchInProgress
                prevFilter = filter
                state.postValue(TrackRepositoryState.searchInProgress)
                tracksInteractor.searchTracks(filter, locale,
                    object : TracksInteractor.TracksConsumer {
                        override fun consume(trackSearchResult: TrackSearchResult) {
                            state.postValue(trackSearchResult.state)
                        }
                    })
            }
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

    fun showPlayer(track: Track) {

        if (isNowPausingBetweenClicks) {
            return
        }

        isNowPausingBetweenClicks = true
        mainHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    isNowPausingBetweenClicks = false
                }
            }, CLICK_DELAY
        )

        showPlayer.postValue(track)
    }
}