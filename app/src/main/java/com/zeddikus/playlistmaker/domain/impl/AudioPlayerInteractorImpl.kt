package com.zeddikus.playlistmaker.domain.impl

import com.zeddikus.playlistmaker.domain.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.models.PlayerState
import com.zeddikus.playlistmaker.domain.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.models.MediaPlayerProgress
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerInteractorImpl(mediaPlayerRepository: MediaPlayerRepository): MediaPlayer {
//    val mediaPlayerRequest: MediaPlayerRequest,
//    val onSetNewState: (PlayerState) -> Unit?) : MediaPlayerAPI {
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
        if (mediaPlayer == null) {
            setNewState(PlayerState.PREPAIRING_ERROR)
        } else {
            setNewState(PlayerState.PREPAIRING)
            mediaPlayer.setDataSource(urlTrack)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                setNewState(PlayerState.PREPARED)
            }
            mediaPlayer.setOnCompletionListener {
                setNewState(PlayerState.PREPARED)
            }
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
