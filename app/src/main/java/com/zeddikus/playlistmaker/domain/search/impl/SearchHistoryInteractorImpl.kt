package com.zeddikus.playlistmaker.domain.search.impl

import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class SearchHistoryInteractorImpl(
    val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override val SP_SEARCH_HISTORY = searchHistoryRepository.getSpSearchHistoryKey()

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    override fun saveHistory(trackList: List<Track>){
        searchHistoryRepository.saveHistory(trackList)
    }

    override fun addTrackToHistory(track: Track){
        searchHistoryRepository.addTrackToHistory(track)
    }

}

