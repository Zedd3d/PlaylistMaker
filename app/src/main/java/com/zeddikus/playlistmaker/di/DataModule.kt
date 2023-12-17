package com.zeddikus.playlistmaker.di

import com.zeddikus.playlistmaker.data.search.api.ItunesAPI
import com.zeddikus.playlistmaker.data.search.api.NetworkClient
import com.zeddikus.playlistmaker.data.sharing.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesAPI> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPI::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

}