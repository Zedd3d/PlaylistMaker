package com.zeddikus.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    fun findTracks(@Query("term") text: String): Call<TracksResponse>
}