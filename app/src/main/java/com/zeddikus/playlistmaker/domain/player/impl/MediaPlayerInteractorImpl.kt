package com.zeddikus.playlistmaker.domain.player.impl

import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(mediaPlayerRepository: MediaPlayerRepository) : MediaPlayer {

    private var urlTrack = ""
    private val mediaPlayer = mediaPlayerRepository.getMediaPlayer()
    private var consumer: ((PlayerState) -> Unit?)? = null


    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun preparePlayer(url: String) {
        urlTrack = url

        try {
            setNewState(PlayerState.PREPAIRING)
            mediaPlayer.setDataSource(urlTrack)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                setNewState(PlayerState.PREPARED)
            }
            mediaPlayer.setOnCompletionListener {
                setNewState(PlayerState.PREPARED)
            }
        } catch (e: Exception) {
            setNewState(PlayerState.PREPAIRING_ERROR)
        }

    }

    override fun setNewState(state: PlayerState){
        consumer?.invoke(state)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentProgress(): MediaPlayerProgress {
        return MediaPlayerProgress(mediaPlayer.currentPosition / 1000, SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
    }

    override fun setConsumer(consumer: (PlayerState) -> Unit?) {
        this.consumer = consumer
    }

}
