package com.zeddikus.playlistmaker.data.search.api

import com.zeddikus.playlistmaker.data.search.dto.TrackSearchRequest
import com.zeddikus.playlistmaker.data.sharing.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: TrackSearchRequest): Response
}
