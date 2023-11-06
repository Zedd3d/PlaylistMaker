package com.zeddikus.playlistmaker.data.impl

import com.google.gson.Gson
import com.zeddikus.playlistmaker.domain.api.JsonHandlerRepository

class JsonHandlerRepositoryImpl: JsonHandlerRepository {
    override fun getJsonHandler(): Gson {
        return Gson()
    }
}