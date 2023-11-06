package com.zeddikus.playlistmaker.data.impl

import com.zeddikus.playlistmaker.data.sharedpref.SharedPrefHandlerImpl
import com.zeddikus.playlistmaker.domain.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl: SearchHistoryRepository {
    override fun clearHistory(){
        SharedPrefHandlerImpl.clearHistory()
    }

    override fun getHistory(): List<Track> {
        return SharedPrefHandlerImpl.getHistory()
    }

    override fun saveHistory(trackList: List<Track>){
        SharedPrefHandlerImpl.saveHistory(trackList)
    }

    override fun addTrackToHistory(track: Track){
        val trackList  = ArrayList<Track>()
        trackList.addAll(getHistory())

        for (element in trackList){
            if (element.trackId == track.trackId){
                trackList.remove(element)
                break
            }
        }

        trackList.add(0,track)

        if (trackList.count()>10){
            trackList.removeLast()
        }
        SharedPrefHandlerImpl.saveHistory(trackList.toList())
    }

    override fun getSpSearchHistoryKey(): String {
        return SharedPrefHandlerImpl.SP_SEARCH_HISTORY
    }
}