package com.zeddikus.playlistmaker.domain.player.api

import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState

interface MediaPlayer {
    fun start()

    fun pause()

    fun stop()

    fun preparePlayer(url: String)

    fun release()

    fun getCurrentProgress(): MediaPlayerProgress

    fun setConsumer(consumer: (PlayerState) -> Unit?)

    fun setNewState(state: PlayerState)
    fun clearProgress()
}