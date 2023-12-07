package com.zeddikus.playlistmaker.domain.player.api

import android.media.MediaPlayer

interface MediaPlayerRepository {
    fun getMediaPlayer(): MediaPlayer
}