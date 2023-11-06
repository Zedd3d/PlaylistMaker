package com.zeddikus.playlistmaker.data.network

import com.zeddikus.playlistmaker.data.NetworkClient
import com.zeddikus.playlistmaker.data.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesAPI = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val resp = itunesAPI.findTracks(dto.expression, dto.locale).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } catch (e:IOException){
                return Response().apply { resultCode = 500 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}