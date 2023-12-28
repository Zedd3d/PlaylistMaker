package com.zeddikus.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class PlayerViewModel(
    track: Track,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val trackTimeUpdater = object : Runnable {
        override fun run() {
            updateTrackTime()
            mainHandler.postDelayed(
                this, TOKEN_TIMER,
                UPDATE_TRACK_TIME_DELAY
            )
        }
    }

    private var onLoad = MutableLiveData<Track>()
    private val state = MutableLiveData<PlayerState>()
    private val currentProgress = MutableLiveData<MediaPlayerProgress>()

    init {
        onLoad.value = track
        mediaPlayer.setConsumer { playerState -> setPlayerState(playerState) }
        mediaPlayer.preparePlayer(track.previewUrl)
        clearProgress()
    }

    private companion object {
        private const val TOKEN_TIMER = "Token_timer"
        private val mainHandler = Handler(Looper.getMainLooper())
        private const val UPDATE_TRACK_TIME_DELAY = 300L
    }

    fun onLoad(): LiveData<Track> = onLoad

    fun getState(): LiveData<PlayerState> = state

    fun getProgress(): LiveData<MediaPlayerProgress> = currentProgress

    fun startPlayer() {
        mediaPlayer.start()
        mainHandler.postDelayed(
            trackTimeUpdater, TOKEN_TIMER, UPDATE_TRACK_TIME_DELAY
        )
        //trackTimeUpdater.run()
        state.postValue(PlayerState.PLAYING)
    }

    fun updateTrackTime() {
        val curProgress = mediaPlayer.getCurrentProgress()
        currentProgress.postValue(curProgress)
    }

    fun pausedPlayer() {
        mediaPlayer.pause()
        stopTimer()
        state.postValue(PlayerState.PAUSED)
    }

    fun release() {
        mediaPlayer.release()
    }

    fun stopPlayer() {
        clearProgress()
        mediaPlayer.stop()
        stopTimer()
    }

    fun pushPlayButton() {
        when (state.value) {
            PlayerState.PAUSED -> startPlayer()
            PlayerState.STOPPED -> startPlayer()
            PlayerState.PREPARED -> startPlayer()
            PlayerState.PLAYING -> pausedPlayer()
            else -> null
        }
    }

    fun stopTimer() {
        mainHandler.removeCallbacks(trackTimeUpdater, TOKEN_TIMER)
    }

    fun clearProgress() {
        stopTimer()
        currentProgress.value = MediaPlayerProgress(0, "00:00")
    }

    fun setPlayerState(consumedState: PlayerState) {
        when (consumedState) {
            PlayerState.PREPARED -> {
                clearProgress()
            }

            PlayerState.PLAYING -> startPlayer()
            PlayerState.PAUSED -> pausedPlayer()
            PlayerState.STOPPED -> stopPlayer()
            else -> null
        }
        state.postValue(consumedState)
    }
}