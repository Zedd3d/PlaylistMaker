package com.zeddikus.playlistmaker.domain.impl

import com.zeddikus.playlistmaker.domain.api.JsonHandlerInteractor
import com.zeddikus.playlistmaker.domain.api.JsonHandlerRepository
import java.lang.reflect.Type

class JsonHandlerInteractorInteractorImpl(jsonHandlerRepository: JsonHandlerRepository): JsonHandlerInteractor {

    private val jsonHandler = jsonHandlerRepository.getJsonHandler()

    override fun <T> fromJson(json: String, typeOfT: Type): T {
        return jsonHandler.fromJson<T>(json, typeOfT)
    }

    override fun <T> toJson(ob: T): String {
        return jsonHandler.toJson(ob)
    }
}