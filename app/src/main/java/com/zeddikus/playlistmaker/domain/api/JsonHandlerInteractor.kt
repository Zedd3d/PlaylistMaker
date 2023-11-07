package com.zeddikus.playlistmaker.domain.api

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

interface JsonHandlerInteractor {
    fun <T> fromJson(json: String,typeOfT: Type): T

    fun <T> toJson(ob:T): String
}

inline fun <reified T> JsonHandlerInteractor.genericType() = object: TypeToken<T>() {}.type