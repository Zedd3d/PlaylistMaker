package com.zeddikus.playlistmaker.data.impl

import android.media.MediaPlayer
import com.zeddikus.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository{
    override fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}