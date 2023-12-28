package com.zeddikus.playlistmaker.domain.player.impl

import android.util.Log
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayerRepository
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(mediaPlayerRepository: MediaPlayerRepository) : MediaPlayer {

    private var urlTrack = ""
    private var startMoment = 0L
    private var progressTime = 0L
    private val mediaPlayer = mediaPlayerRepository.getMediaPlayer()
    private var consumer: ((PlayerState) -> Unit?)? = null


    override fun start() {
        startMoment = System.currentTimeMillis()
        mediaPlayer.start()
    }

    override fun pause() {
        progressTime = progressTime + (System.currentTimeMillis() - startMoment)
        mediaPlayer.pause()
    }

    override fun stop() {
        progressTime = 0L
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
        //val currentPosition = mediaPlayer.currentPosition

        val currentPosition = (System.currentTimeMillis() - startMoment + progressTime)

        val p = (currentPosition / 1000).toInt()
        val d = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)

        Log.v("timerTick", "cp " + currentPosition)
        return MediaPlayerProgress(p, d)
    }

    override fun setConsumer(consumer: (PlayerState) -> Unit?) {
        this.consumer = consumer
    }

}
