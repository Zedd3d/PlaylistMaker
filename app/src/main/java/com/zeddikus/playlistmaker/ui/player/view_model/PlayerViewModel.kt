package com.zeddikus.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.FavoritesInteractor
import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.TrackInPlaylistState
import com.zeddikus.playlistmaker.domain.player.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.player.models.TrackState
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    track: Track,
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var onLoad = MutableLiveData<TrackState>()
    private val state = MutableLiveData<PlayerState>()
    private val currentProgress = MutableLiveData<MediaPlayerProgress>()
    private var timerJob: Job? = null
    private var getJob: Job? = null

    private val showPlaylists = SingleLiveEvent<List<Playlist>>()
    private val trackAdditionStatus = SingleLiveEvent<TrackInPlaylistState>()

    init {
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isFavorite(track.trackId)

            onLoad.postValue(TrackState(track, isFavorite))
        }

        mediaPlayer.setConsumer { playerState -> setPlayerState(playerState) }
        mediaPlayer.preparePlayer(track.previewUrl)
        clearProgress()
    }

    private companion object {
        private const val CLICK_DELAY = 300L
        private const val UPDATE_TRACK_TIME_DELAY = 300L
    }

    fun onLoad(): LiveData<TrackState> = onLoad

    fun getState(): LiveData<PlayerState> = state

    fun getProgress(): LiveData<MediaPlayerProgress> = currentProgress

    fun getStateAddingToPlaylists(): SingleLiveEvent<List<Playlist>> = showPlaylists

    fun getTrackAdditionStatus(): SingleLiveEvent<TrackInPlaylistState> = trackAdditionStatus


    fun startPlayer() {
        mediaPlayer.start()
        state.value = PlayerState.PLAYING
        timerJob = viewModelScope.launch {
            while (state.value == PlayerState.PLAYING) {
                delay(UPDATE_TRACK_TIME_DELAY)
                updateTrackTime()
            }
        }
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
        timerJob?.cancel()

    }

    fun clearProgress() {
        stopTimer()
        mediaPlayer.clearProgress()
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

    fun addingToFavorites() {
        if (onLoad.value == null) return

        val track = onLoad.value!!.track

        viewModelScope.launch {
            favoritesInteractor.switchFavoriteProperty(track)
            onLoad.postValue(TrackState(track, favoritesInteractor.isFavorite(track.trackId)))
        }
    }

    fun addingToPlaylist() {
        getJob?.cancel()
        getJob = viewModelScope.launch {
            delay(CLICK_DELAY)

            playlistsInteractor.getPlaylists()
                .collect { playlists ->
                    showPlaylists.postValue(playlists)
                }
        }

    }

    fun addTrackToPlayList(playlist: Playlist) {

        viewModelScope.launch {
            onLoad.value?.track?.let {
                if (playlistsInteractor.trackIsAlreadyOnThePlaylist(playlist, it)) {
                    trackAdditionStatus.postValue(TrackInPlaylistState.AleradyIn(playlist.playlistName))
                } else {
                    playlistsInteractor.addToPlayList(playlist, it)
                    trackAdditionStatus.postValue(TrackInPlaylistState.AdditionSuccessful(playlist.playlistName))
                }
            }
        }
    }

}