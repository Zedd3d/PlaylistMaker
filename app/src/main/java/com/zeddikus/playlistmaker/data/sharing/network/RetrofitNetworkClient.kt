package com.zeddikus.playlistmaker.data.sharing.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.zeddikus.playlistmaker.data.search.api.ItunesAPI
import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.sharing.dto.Response
import java.io.IOException

class RetrofitNetworkClient(
    private val context: Context,
    private val itunesAPI: ItunesAPI
) : NetworkClient {

    override suspend fun doRequest(dto: TrackSearchRequest): Response {
        if (isConnected() == false) return Response().apply { resultCode = -1 }

        return try {
            val resp = itunesAPI.findTracks(dto.expression, dto.locale)
            resp.apply { resultCode = 200 }
        } catch (e: IOException) {
            Response().apply { resultCode = 500 }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

}