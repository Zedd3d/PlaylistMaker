package com.zeddikus.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zeddikus.playlistmaker.creator.Creator
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class PlayerViewModel(track: Track) : ViewModel() {
    private val mediaPlayer = Creator.provideAudioPlayerInteractor()
    private val trackTimeUpdater = object : Runnable {
        override fun run() {
            updateTrackTime()
            mainHandler.postDelayed(
                this,
                UPDATE_TRACK_TIME_DELAY
            )
        }
    }

    private val state = MutableLiveData<PlayerState>()
    private val currentProgress = MutableLiveData<MediaPlayerProgress>()

    init {
        mediaPlayer.setConsumer { playerState -> state.postValue(playerState) }
        mediaPlayer.preparePlayer(track.previewUrl)
    }

    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
        private const val UPDATE_TRACK_TIME_DELAY = 300L
    }

    fun getState(): LiveData<PlayerState> = state

    fun getProgress(): LiveData<MediaPlayerProgress> = currentProgress

    fun startPlayer() {
        mediaPlayer.start()
        mainHandler.postDelayed(
            trackTimeUpdater, UPDATE_TRACK_TIME_DELAY
        )
    }

    fun updateTrackTime() {
        currentProgress.postValue(mediaPlayer.getCurrentProgress())
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
        mediaPlayer.stop()
        stopTimer()
    }

    fun pushPlayButton() {
        when (state.value) {
            PlayerState.PAUSED -> state.postValue(PlayerState.PLAYING)
            PlayerState.STOPPED -> state.postValue(PlayerState.PLAYING)
            PlayerState.PREPARED -> state.postValue(PlayerState.PLAYING)
            PlayerState.PLAYING -> state.postValue(PlayerState.PAUSED)
            else -> null
        }
    }

    fun stopTimer() {
        mainHandler.removeCallbacks(trackTimeUpdater)
    }

    fun clearProgress() {
        stopTimer()
        currentProgress.value = MediaPlayerProgress(0, "00:00")
    }

}