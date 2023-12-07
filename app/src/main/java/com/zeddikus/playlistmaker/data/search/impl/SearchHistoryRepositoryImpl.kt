package com.zeddikus.playlistmaker.data.search.impl

import com.zeddikus.playlistmaker.data.sharing.impl.SharedPreferencesImpl
import com.zeddikus.playlistmaker.domain.search.api.SearchHistoryRepository
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class SearchHistoryRepositoryImpl: SearchHistoryRepository {
    override fun clearHistory(){
        SharedPreferencesImpl.clearHistory()
    }

    override fun getHistory(): List<Track> {
        return SharedPreferencesImpl.getHistory()
    }

    override fun saveHistory(trackList: List<Track>){
        SharedPreferencesImpl.saveHistory(trackList)
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
        SharedPreferencesImpl.saveHistory(trackList.toList())
    }

    override fun getSpSearchHistoryKey(): String {
        return SharedPreferencesImpl.SP_SEARCH_HISTORY
    }
}