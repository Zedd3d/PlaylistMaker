package com.zeddikus.playlistmaker.domain.api

import android.media.MediaPlayer

interface MediaPlayerRepository {
    fun getMediaPlayer(): MediaPlayer
}