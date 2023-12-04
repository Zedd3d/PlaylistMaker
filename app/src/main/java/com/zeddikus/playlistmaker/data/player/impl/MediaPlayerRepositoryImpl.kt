package com.zeddikus.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {
    override fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}