package com.zeddikus.playlistmaker.data

import com.zeddikus.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}
