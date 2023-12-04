package com.zeddikus.playlistmaker.data.search.api

import com.zeddikus.playlistmaker.data.sharing.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}
