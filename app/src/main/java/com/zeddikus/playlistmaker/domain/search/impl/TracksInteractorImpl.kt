package com.zeddikus.playlistmaker.domain.search.impl

import com.zeddikus.playlistmaker.domain.search.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.search.api.TracksRepository
import java.util.concurrent.Executors


class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, locale: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression,locale))
        }
    }

}