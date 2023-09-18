package com.zeddikus.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryHandler(app: App) {
    val sharedPref = app.sharedPref
    val SP_SEARCH_HISTORY = app.SP_SEARCH_HISTORY

    fun clearHistory(){
        sharedPref.edit().remove(SP_SEARCH_HISTORY).apply()
    }

    fun getHistory(): List<Track> {
        return Gson().fromJson<List<Track>>(sharedPref.getString(SP_SEARCH_HISTORY,"[]"), object: TypeToken<List<Track>>(){}.type)
    }

    fun saveHistory(trackList: List<Track>){
        val json = Gson().toJson(trackList)
        sharedPref.edit().putString(SP_SEARCH_HISTORY,json).apply()
    }

    fun addTrackToHistory(track: Track){
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
        saveHistory(trackList.toList())
    }
}

