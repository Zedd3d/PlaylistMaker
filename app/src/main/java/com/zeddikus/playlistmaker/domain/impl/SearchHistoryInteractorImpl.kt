package com.zeddikus.playlistmaker.domain.impl

import com.zeddikus.playlistmaker.domain.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(val searchHistoryRepository: SearchHistoryRepository): SearchHistoryInteractor {

    override val SP_SEARCH_HISTORY = searchHistoryRepository.getSpSearchHistoryKey()

    override fun clearHistory(){
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

