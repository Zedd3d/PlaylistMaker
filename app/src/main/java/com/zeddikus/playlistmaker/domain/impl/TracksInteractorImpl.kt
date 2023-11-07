package com.zeddikus.playlistmaker.domain.impl

import com.zeddikus.playlistmaker.domain.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors


class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, locale: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression,locale))
        }
    }

}