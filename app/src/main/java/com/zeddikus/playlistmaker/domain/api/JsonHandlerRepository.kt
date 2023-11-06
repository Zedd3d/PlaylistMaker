package com.zeddikus.playlistmaker.domain.api

import com.google.gson.Gson

interface JsonHandlerRepository {
    fun getJsonHandler(): Gson
}